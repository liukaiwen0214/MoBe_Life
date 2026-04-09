package com.mobe.mobe_life_backend.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private static final String BIZ_TYPE_BIND_EMAIL = "BIND_EMAIL";
  private static final Integer TARGET_TYPE_EMAIL = 2;
  private static final Integer CHANNEL_EMAIL = 2;

  private final MobeUserService mobeUserService;
  private final WechatMiniAppService wechatMiniAppService;
  private final EmailService emailService;
  private final VerificationCodeMapper verificationCodeMapper;
  private final MessageSendLogMapper messageSendLogMapper;

  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
      user.setNickname("微信用户");
      user.setStatus(0);
      user.setGender(0);
      user.setIsDeleted(0);
      mobeUserService.save(user);
    } else if (unionid != null && !unionid.isBlank() && user.getUnionid() == null) {
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

  @Override
  public void logout() {
    // 第一版JWT无状态，服务端暂不做额外处理
  }

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

  @Override
  public void sendBindEmailCode(SendEmailCodeDTO sendEmailCodeDTO) {
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
            .set(VerificationCode::getStatus, 3)
            .set(VerificationCode::getRemark, "新验证码发送后作废旧验证码"));

    String code = VerificationCodeUtils.generateCode();
    String codeHash = VerificationCodeUtils.hashCode(email, BIZ_TYPE_BIND_EMAIL, code);
    LocalDateTime now = LocalDateTime.now();

    MessageSendLog messageSendLog = new MessageSendLog();
    messageSendLog.setChannel(CHANNEL_EMAIL);
    messageSendLog.setBizType(BIZ_TYPE_BIND_EMAIL);
    messageSendLog.setTarget(email);
    messageSendLog.setProvider("TencentCloudSES");
    messageSendLog.setSendStatus(0);
    messageSendLog.setPlatform("miniapp");
    messageSendLog.setRequestContent("send bind email code");
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
      emailService.sendBindEmailCode(email, code);

      messageSendLog.setSendStatus(1);
      messageSendLog.setSendTime(LocalDateTime.now());
      messageSendLog.setResponseContent("success");
      messageSendLogMapper.updateById(messageSendLog);
    } catch (Exception e) {
      messageSendLog.setSendStatus(2);
      messageSendLog.setFailReason(e.getMessage());
      messageSendLog.setResponseContent(e.getMessage());
      messageSendLogMapper.updateById(messageSendLog);

      throw new BusinessException("发送验证码失败：" + e.getMessage());
    }
  }

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

}