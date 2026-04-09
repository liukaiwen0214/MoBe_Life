/**
 * 核心职责：实现认证中心的核心业务流程，包括微信登录、账号绑定、验证码校验和密码管理。
 * 所属业务模块：认证中心 / 业务服务实现。
 * 重要依赖关系或外部约束：依赖微信接口、邮件通道、验证码表、消息日志表和用户表；
 * 业务正确性依赖数据库状态与 JWT 约定一致。
 */
package com.mobe.mobe_life_backend.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mobe.mobe_life_backend.auth.config.TencentSesProperties;
import com.mobe.mobe_life_backend.auth.dto.BindEmailDTO;
import com.mobe.mobe_life_backend.auth.dto.BindPhoneDTO;
import com.mobe.mobe_life_backend.auth.dto.ChangePasswordDTO;
import com.mobe.mobe_life_backend.auth.dto.SendEmailCodeDTO;
import com.mobe.mobe_life_backend.auth.dto.SetPasswordDTO;
import com.mobe.mobe_life_backend.auth.dto.WxMiniLoginDTO;
import com.mobe.mobe_life_backend.auth.entity.MessageSendLog;
import com.mobe.mobe_life_backend.auth.entity.VerificationCode;
import com.mobe.mobe_life_backend.auth.mapper.MessageSendLogMapper;
import com.mobe.mobe_life_backend.auth.mapper.VerificationCodeMapper;
import com.mobe.mobe_life_backend.auth.service.AuthService;
import com.mobe.mobe_life_backend.auth.service.EmailService;
import com.mobe.mobe_life_backend.auth.service.WechatMiniAppService;
import com.mobe.mobe_life_backend.auth.vo.EmailSendResult;
import com.mobe.mobe_life_backend.auth.vo.LoginUserVO;
import com.mobe.mobe_life_backend.auth.vo.TokenVO;
import com.mobe.mobe_life_backend.auth.vo.WxCode2SessionVO;
import com.mobe.mobe_life_backend.auth.vo.WxPhoneNumberVO;
import com.mobe.mobe_life_backend.common.context.UserContext;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.common.utils.JwtUtils;
import com.mobe.mobe_life_backend.common.utils.VerificationCodeUtils;
import com.mobe.mobe_life_backend.user.entity.MobeUser;
import com.mobe.mobe_life_backend.user.service.MobeUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 认证服务实现。
 *
 * <p>设计初衷是把“用户身份建立”和“账号安全增强”收敛在同一应用服务中，
 * 使 Controller 看见的是稳定用例，而底层数据库与第三方系统交互则被完整封装。</p>
 *
 * <p>核心业务概念：</p>
 * <p>1. 微信登录以 `openid` 为当前小程序内唯一身份锚点，首次登录自动建档。</p>
 * <p>2. 邮箱验证码以“目标 + 业务类型 + 验证码”摘要存储，避免明文落库。</p>
 * <p>3. 密码设置与密码修改分离，避免未校验旧密码的逻辑误用于改密场景。</p>
 *
 * <p>线程安全性：本类作为 Spring 单例 Bean 使用，本身不保存请求级可变状态；数据库状态变更依赖事务和约束保持一致性。
 * 当前类未显式声明事务，若未来在更复杂的消息补偿场景下扩展，应重新评估发送日志与验证码记录的一致性边界。</p>
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  /**
   * 邮箱绑定场景编码。
   * 明确区分业务类型，避免不同验证码场景相互串用。
   */
  private static final String BIZ_TYPE_BIND_EMAIL = "BIND_EMAIL";

  /**
   * 邮箱目标类型编码。
   * 与 `verification_code.target_type` 的历史约定保持一致。
   */
  private static final Integer TARGET_TYPE_EMAIL = 2;

  /**
   * 邮件渠道编码。
   * 与消息发送日志中的渠道枚举约定保持一致。
   */
  private static final Integer CHANNEL_EMAIL = 2;

  /**
   * 用户域服务。
   * 负责用户查询与持久化，认证服务通过它维护账号资料。
   */
  private final MobeUserService mobeUserService;

  /**
   * 微信小程序服务。
   * 承担所有微信远程接口细节。
   */
  private final WechatMiniAppService wechatMiniAppService;

  /**
   * 邮件服务。
   * 负责实际发送验证码邮件。
   */
  private final EmailService emailService;

  /**
   * 验证码持久化入口。
   */
  private final VerificationCodeMapper verificationCodeMapper;

  /**
   * 消息发送日志持久化入口。
   */
  private final MessageSendLogMapper messageSendLogMapper;

  /**
   * 腾讯云 SES 配置。
   * 主要用于回写模板编号到消息日志。
   */
  private final TencentSesProperties tencentSesProperties;

  /**
   * 密码编码器。
   * 采用 BCrypt 是为了让同一明文在每次加密时都生成不同密文，降低撞库风险。
   */
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  /**
   * 微信小程序登录。
   *
   * @param wxMiniLoginDTO 登录参数，不允许为 null；`code` 必须为有效微信临时凭证。
   * @return 登录结果，不返回 null；包含用户标识、展示信息和 JWT。
   * @throws BusinessException 当微信登录失败时抛出。
   * @implNote 该方法可能创建新用户，并更新既有用户缺失的 `unionid`。
   */
  @Override
  public LoginUserVO wxMiniLogin(WxMiniLoginDTO wxMiniLoginDTO) {
    WxCode2SessionVO wxSession = wechatMiniAppService.code2Session(wxMiniLoginDTO.getCode());

    String openid = wxSession.getOpenid();
    String unionid = wxSession.getUnionid();

    MobeUser user = mobeUserService.getOne(
        new LambdaQueryWrapper<MobeUser>().eq(MobeUser::getOpenid, openid));

    if (user == null) {
      user = new MobeUser();
      user.setOpenid(openid);
      user.setUnionid(unionid);
      // 首次建档先给出稳定默认值，保证前端登录成功后无需额外判空分支才能展示。
      user.setNickname("微信用户");
      user.setStatus(0);
      user.setGender(0);
      user.setIsDeleted(0);
      mobeUserService.save(user);
    } else if (unionid != null && !unionid.isBlank() && user.getUnionid() == null) {
      // 只在历史数据缺失时补写 unionid，避免把已建立的跨应用关联信息被错误覆盖。
      user.setUnionid(unionid);
      mobeUserService.updateById(user);
    }

    String token = JwtUtils.createToken(user.getId());

    LoginUserVO loginUserVO = new LoginUserVO();
    loginUserVO.setUserId(user.getId());
    loginUserVO.setNickname(user.getNickname());
    loginUserVO.setAvatar(user.getAvatar());
    loginUserVO.setToken(token);
    return loginUserVO;
  }

  /**
   * 刷新 token。
   *
   * @param authorization 请求头中的授权信息，不允许为空。
   * @return 新 token 结果，不返回 null。
   * @throws BusinessException 当 token 为空、无效或已过期时抛出。
   * @implNote 当前实现不引入 refresh token 黑名单，因此只要旧 token 仍有效就可以刷新。
   */
  @Override
  public TokenVO refreshToken(String authorization) {
    if (authorization == null || authorization.isBlank()) {
      throw new BusinessException("未登录或token为空");
    }

    String token = authorization;
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (!JwtUtils.isValid(token)) {
      throw new BusinessException("token无效或已过期");
    }

    Long userId = JwtUtils.getUserId(token);
    String newToken = JwtUtils.createToken(userId);

    TokenVO tokenVO = new TokenVO();
    tokenVO.setToken(newToken);
    return tokenVO;
  }

  /**
   * 登出。
   *
   * @implNote 当前版本采用无状态 JWT，服务端没有黑名单或会话表，因此此处显式保持空实现。
   */
  @Override
  public void logout() {
    // 第一版 JWT 选择无状态实现，优先降低系统复杂度；真正失效依赖客户端丢弃 token 与自然过期。
  }

  /**
   * 绑定手机号。
   *
   * @param bindPhoneDTO 绑定参数，不允许为 null。
   * @throws BusinessException 当用户未登录、手机号获取失败、手机号被占用或用户不存在时抛出。
   * @implNote 该方法会调用微信接口并更新用户手机号字段。
   */
  @Override
  public void bindPhone(BindPhoneDTO bindPhoneDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException("当前用户未登录");
    }

    WxPhoneNumberVO phoneInfo = wechatMiniAppService.getPhoneNumber(bindPhoneDTO.getCode());
    String phone = phoneInfo.getPurePhoneNumber();
    if (phone == null || phone.isBlank()) {
      throw new BusinessException("获取手机号失败");
    }

    MobeUser exist = mobeUserService.getOne(
        new LambdaQueryWrapper<MobeUser>().eq(MobeUser::getPhone, phone));
    if (exist != null && !exist.getId().equals(userId)) {
      throw new BusinessException("该手机号已绑定其他账号");
    }

    MobeUser currentUser = mobeUserService.getById(userId);
    if (currentUser == null) {
      throw new BusinessException("用户不存在");
    }

    currentUser.setPhone(phone);
    mobeUserService.updateById(currentUser);
  }

  /**
   * 发送绑定邮箱验证码。
   *
   * @param sendEmailCodeDTO 发送参数，不允许为 null。
   * @param request 当前请求，允许为 null；用于记录请求来源 IP。
   * @throws BusinessException 当用户未登录、邮箱被占用、发送过于频繁或邮件发送失败时抛出。
   * @implNote 该方法会创建消息日志、创建验证码记录，并调用邮件通道。
   */
  @Override
  public void sendBindEmailCode(SendEmailCodeDTO sendEmailCodeDTO, HttpServletRequest request) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException("当前用户未登录");
    }

    String email = sendEmailCodeDTO.getEmail().trim().toLowerCase();

    MobeUser exist = mobeUserService.getOne(
        new LambdaQueryWrapper<MobeUser>()
            .eq(MobeUser::getEmail, email));
    if (exist != null && !exist.getId().equals(userId)) {
      throw new BusinessException("该邮箱已被其他账号绑定");
    }

    VerificationCode latestCode = verificationCodeMapper.selectOne(
        new LambdaQueryWrapper<VerificationCode>()
            .eq(VerificationCode::getTarget, email)
            .eq(VerificationCode::getTargetType, TARGET_TYPE_EMAIL)
            .eq(VerificationCode::getBizType, BIZ_TYPE_BIND_EMAIL)
            .eq(VerificationCode::getStatus, 0)
            .eq(VerificationCode::getIsDeleted, 0)
            .orderByDesc(VerificationCode::getId)
            .last("limit 1"));

    if (latestCode != null && latestCode.getSendTime() != null
        && latestCode.getSendTime().plusSeconds(60).isAfter(LocalDateTime.now())) {
      throw new BusinessException("发送过于频繁，请稍后再试");
    }

    verificationCodeMapper.update(
        null,
        new LambdaUpdateWrapper<VerificationCode>()
            .eq(VerificationCode::getTarget, email)
            .eq(VerificationCode::getTargetType, TARGET_TYPE_EMAIL)
            .eq(VerificationCode::getBizType, BIZ_TYPE_BIND_EMAIL)
            .eq(VerificationCode::getStatus, 0)
            .eq(VerificationCode::getIsDeleted, 0)
            // 新验证码生成前先把旧验证码统一作废，避免用户邮箱里存在多个同时可用的验证码。
            .set(VerificationCode::getStatus, 3)
            .set(VerificationCode::getRemark, "新验证码发送后作废旧验证码"));

    String code = VerificationCodeUtils.generateCode();
    String codeHash = VerificationCodeUtils.hashCode(email, BIZ_TYPE_BIND_EMAIL, code);
    LocalDateTime now = LocalDateTime.now();
    String requestIp = getRequestIp(request);

    MessageSendLog messageSendLog = new MessageSendLog();
    messageSendLog.setChannel(CHANNEL_EMAIL);
    messageSendLog.setBizType(BIZ_TYPE_BIND_EMAIL);
    messageSendLog.setTarget(email);
    messageSendLog.setTemplateCode(String.valueOf(tencentSesProperties.getTemplateId()));
    messageSendLog.setProvider("TencentCloudSES");
    messageSendLog.setSendStatus(0);
    messageSendLog.setRetryCount(0);
    messageSendLog.setRequestIp(requestIp);
    messageSendLog.setPlatform("miniapp");
    // 这里只记录业务摘要，不记录验证码明文，避免数据库和日志同时暴露敏感信息。
    messageSendLog.setRequestContent("send bind email code to " + email);
    messageSendLog.setRemark("邮箱验证码待发送");
    messageSendLog.setIsDeleted(0);
    messageSendLogMapper.insert(messageSendLog);

    VerificationCode verificationCode = new VerificationCode();
    verificationCode.setTarget(email);
    verificationCode.setTargetType(TARGET_TYPE_EMAIL);
    verificationCode.setBizType(BIZ_TYPE_BIND_EMAIL);
    verificationCode.setCodeHash(codeHash);
    verificationCode.setCodePreview(code.substring(0, 2) + "****");
    verificationCode.setStatus(0);
    verificationCode.setExpireTime(now.plusMinutes(10));
    verificationCode.setSendTime(now);
    verificationCode.setPlatform("miniapp");
    verificationCode.setFailCount(0);
    verificationCode.setMessageLogId(messageSendLog.getId());
    verificationCode.setIsDeleted(0);
    verificationCodeMapper.insert(verificationCode);

    try {
      EmailSendResult sendResult = emailService.sendBindEmailCode(email, code);

      messageSendLog.setSendStatus(1);
      messageSendLog.setSendTime(LocalDateTime.now());
      messageSendLog.setProviderMessageId(sendResult.getProviderMessageId());
      messageSendLog.setResponseContent(sendResult.getResponseContent());
      messageSendLog.setFailReason(null);
      messageSendLog.setRemark("邮箱验证码发送成功");
      messageSendLogMapper.updateById(messageSendLog);
    } catch (Exception e) {
      // 即使邮件发送失败，也保留发送日志和验证码记录，便于排障与后续补偿任务接管。
      messageSendLog.setSendStatus(2);
      messageSendLog.setProviderMessageId(null);
      messageSendLog.setFailReason(e.getMessage());
      messageSendLog.setResponseContent(e.getMessage());
      messageSendLog.setRemark("邮箱验证码发送失败");
      messageSendLogMapper.updateById(messageSendLog);

      throw new BusinessException("发送验证码失败：" + e.getMessage());
    }
  }

  /**
   * 绑定邮箱。
   *
   * @param bindEmailDTO 绑定参数，不允许为 null。
   * @throws BusinessException 当用户未登录、邮箱被占用、验证码不存在、过期、错误或用户不存在时抛出。
   * @implNote 该方法会修改用户资料与验证码状态。
   */
  @Override
  public void bindEmail(BindEmailDTO bindEmailDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException("当前用户未登录");
    }

    String email = bindEmailDTO.getEmail().trim().toLowerCase();
    String code = bindEmailDTO.getCode().trim();

    MobeUser exist = mobeUserService.getOne(
        new LambdaQueryWrapper<MobeUser>()
            .eq(MobeUser::getEmail, email));
    if (exist != null && !exist.getId().equals(userId)) {
      throw new BusinessException("该邮箱已被其他账号绑定");
    }

    VerificationCode verificationCode = verificationCodeMapper.selectOne(
        new LambdaQueryWrapper<VerificationCode>()
            .eq(VerificationCode::getTarget, email)
            .eq(VerificationCode::getTargetType, TARGET_TYPE_EMAIL)
            .eq(VerificationCode::getBizType, BIZ_TYPE_BIND_EMAIL)
            .eq(VerificationCode::getStatus, 0)
            .eq(VerificationCode::getIsDeleted, 0)
            .orderByDesc(VerificationCode::getId)
            .last("limit 1"));

    if (verificationCode == null) {
      throw new BusinessException("验证码不存在或已失效");
    }

    if (verificationCode.getExpireTime() == null || verificationCode.getExpireTime().isBefore(LocalDateTime.now())) {
      verificationCode.setStatus(2);
      verificationCode.setRemark("验证码已过期");
      verificationCodeMapper.updateById(verificationCode);
      throw new BusinessException("验证码已过期");
    }

    String inputCodeHash = VerificationCodeUtils.hashCode(email, BIZ_TYPE_BIND_EMAIL, code);
    if (!inputCodeHash.equals(verificationCode.getCodeHash())) {
      verificationCode
          .setFailCount((verificationCode.getFailCount() == null ? 0 : verificationCode.getFailCount()) + 1);
      verificationCodeMapper.updateById(verificationCode);
      throw new BusinessException("验证码错误");
    }

    MobeUser currentUser = mobeUserService.getById(userId);
    if (currentUser == null) {
      throw new BusinessException("用户不存在");
    }

    currentUser.setEmail(email);
    mobeUserService.updateById(currentUser);

    verificationCode.setStatus(1);
    verificationCode.setUsedTime(LocalDateTime.now());
    verificationCode.setRemark("邮箱绑定成功");
    verificationCodeMapper.updateById(verificationCode);
  }

  /**
   * 修改密码。
   *
   * @param changePasswordDTO 改密参数，不允许为 null。
   * @throws BusinessException 当用户未登录、账号不允许改密、旧密码错误或新密码不符合业务规则时抛出。
   * @implNote 该方法会更新用户密码密文，不会保存旧密码历史。
   */
  @Override
  public void changePassword(ChangePasswordDTO changePasswordDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException("当前用户未登录");
    }

    MobeUser currentUser = mobeUserService.getById(userId);
    validatePasswordOperationAllowed(currentUser);

    String oldPassword = changePasswordDTO.getOldPassword();
    String newPassword = changePasswordDTO.getNewPassword();
    String confirmPassword = changePasswordDTO.getConfirmPassword();

    if (!newPassword.equals(confirmPassword)) {
      throw new BusinessException("两次输入的新密码不一致");
    }

    if (newPassword.length() < 6) {
      throw new BusinessException("新密码长度不能少于6位");
    }

    String dbPassword = currentUser.getPassword();
    if (dbPassword == null || dbPassword.isBlank()) {
      throw new BusinessException("当前账号尚未设置密码");
    }

    if (!passwordEncoder.matches(oldPassword, dbPassword)) {
      throw new BusinessException("原密码错误");
    }

    if (passwordEncoder.matches(newPassword, dbPassword)) {
      throw new BusinessException("新密码不能与原密码相同");
    }

    currentUser.setPassword(passwordEncoder.encode(newPassword));
    mobeUserService.updateById(currentUser);
  }

  /**
   * 校验账号是否具备密码相关操作资格。
   *
   * @param user 当前用户实体，允许为 null。
   * @throws BusinessException 当用户不存在、已删除或尚未绑定手机号/邮箱时抛出。
   * @implNote 该规则的核心意图是避免“纯微信匿名身份”直接拥有本地密码，降低账号无法找回的风险。
   */
  private void validatePasswordOperationAllowed(MobeUser user) {
    if (user == null || Integer.valueOf(1).equals(user.getIsDeleted())) {
      throw new BusinessException("用户不存在");
    }

    boolean hasPhone = user.getPhone() != null && !user.getPhone().isBlank();
    boolean hasEmail = user.getEmail() != null && !user.getEmail().isBlank();

    if (!hasPhone && !hasEmail) {
      throw new BusinessException("请先绑定手机号或邮箱");
    }
  }

  /**
   * 设置初始密码。
   *
   * @param setPasswordDTO 设置参数，不允许为 null。
   * @throws BusinessException 当用户未登录、账号不允许设密、密码不一致、长度不足或已存在密码时抛出。
   * @implNote 该方法会更新用户密码密文。
   */
  @Override
  public void setPassword(SetPasswordDTO setPasswordDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException("当前用户未登录");
    }

    MobeUser currentUser = mobeUserService.getById(userId);
    validatePasswordOperationAllowed(currentUser);

    String newPassword = setPasswordDTO.getNewPassword();
    String confirmPassword = setPasswordDTO.getConfirmPassword();

    if (!newPassword.equals(confirmPassword)) {
      throw new BusinessException("两次输入的新密码不一致");
    }

    if (newPassword.length() < 6) {
      throw new BusinessException("新密码长度不能少于6位");
    }

    if (currentUser.getPassword() != null && !currentUser.getPassword().isBlank()) {
      throw new BusinessException("当前账号已设置密码");
    }

    currentUser.setPassword(passwordEncoder.encode(newPassword));
    mobeUserService.updateById(currentUser);
  }

  /**
   * 从请求中提取尽量接近真实客户端的 IP。
   *
   * @param request 当前请求，允许为 null。
   * @return 若请求为空则返回 null；否则优先返回 `X-Forwarded-For` 首个地址，再降级到 `X-Real-IP` 和 `remoteAddr`。
   * @implNote 该方法不保证一定得到真实公网 IP，因为可信代理链仍依赖部署层约束。
   */
  private String getRequestIp(HttpServletRequest request) {
    if (request == null) {
      return null;
    }

    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (xForwardedFor != null && !xForwardedFor.isBlank() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
      // 代理链会把多个 IP 以逗号串联，首个通常才是最接近客户端的一跳。
      return xForwardedFor.split(",")[0].trim();
    }

    String xRealIp = request.getHeader("X-Real-IP");
    if (xRealIp != null && !xRealIp.isBlank() && !"unknown".equalsIgnoreCase(xRealIp)) {
      return xRealIp.trim();
    }

    return request.getRemoteAddr();
  }
}
