/**
 * 核心职责：定义认证中心的业务契约，覆盖登录、登录态维护、账号绑定和密码管理。
 * 所属业务模块：认证中心 / 业务服务接口。
 * 重要依赖关系或外部约束：实现类通常会组合微信小程序接口、JWT 工具、验证码表和消息日志表完成业务闭环。
 */
package com.mobe.mobe_life_backend.auth.service;

import com.mobe.mobe_life_backend.auth.dto.BindEmailDTO;
import com.mobe.mobe_life_backend.auth.dto.BindPhoneDTO;
import com.mobe.mobe_life_backend.auth.dto.ChangePasswordDTO;
import com.mobe.mobe_life_backend.auth.dto.CodeLoginDTO;
import com.mobe.mobe_life_backend.auth.dto.PasswordLoginDTO;
import com.mobe.mobe_life_backend.auth.dto.SendEmailCodeDTO;
import com.mobe.mobe_life_backend.auth.dto.SetPasswordDTO;
import com.mobe.mobe_life_backend.auth.dto.WxMiniLoginDTO;
import com.mobe.mobe_life_backend.auth.vo.CaptchaVO;
import com.mobe.mobe_life_backend.auth.vo.LoginUserVO;
import com.mobe.mobe_life_backend.auth.vo.TokenVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 认证服务接口。
 *
 * <p>
 * 设计初衷是在控制层和具体实现之间建立稳定边界，让认证相关用例都围绕“业务动作”建模，
 * 而不是围绕数据库操作或第三方 SDK 细节建模。
 * </p>
 *
 * <p>
 * 线程安全性：接口本身无状态；实现类若以 Spring 单例形式存在，必须避免保存请求级可变状态。
 * </p>
 *
 * <p>
 * 极简使用示例：
 * </p>
 *
 * <pre>{@code
 * LoginUserVO loginUser = authService.wxMiniLogin(dto);
 * TokenVO refreshed = authService.refreshToken(authorizationHeader);
 * }</pre>
 */
public interface AuthService {

  /**
   * 使用微信小程序临时凭证登录或注册账号。
   *
   * @param wxMiniLoginDTO 登录参数，不允许为 null；其中 `code` 必须来自微信 `wx.login` 且尚未过期。
   * @return 登录结果，不返回 null；成功时至少包含用户 ID 和 JWT。
   * @throws RuntimeException 当微信凭证无效、远程接口失败或建档过程出现业务冲突时抛出。
   * @implNote 该方法可能创建新用户记录，并会发起微信远程调用。
   */
  LoginUserVO wxMiniLogin(WxMiniLoginDTO wxMiniLoginDTO);

  /**
   * 刷新当前用户的访问令牌。
   *
   * @param authorization `Authorization` 请求头值，不允许为 null 或空白；支持包含 `Bearer ` 前缀。
   * @return 新 token 结果，不返回 null；当前实现只返回一个新的 JWT。
   * @throws RuntimeException 当 token 缺失、格式非法、校验失败或已过期时抛出。
   * @implNote 该方法不会访问数据库，但会解析和重新生成 JWT。
   */
  TokenVO refreshToken(String authorization);

  /**
   * 执行登出。
   *
   * @throws RuntimeException 当前实现无显式异常；未来若接入黑名单或审计系统，可能引入外部副作用。
   * @implNote 当前版本 JWT 采用无状态方案，服务端不会落登出记录。
   */
  void logout();

  /**
   * 将微信手机号绑定到当前登录用户。
   *
   * @param bindPhoneDTO 绑定参数，不允许为 null；`code` 必须来自微信手机号授权场景。
   * @throws RuntimeException 当当前用户未登录、微信手机号获取失败、手机号已被其他账号占用或用户不存在时抛出。
   * @implNote 该方法会发起微信远程调用并更新用户资料。
   */
  void bindPhone(BindPhoneDTO bindPhoneDTO);

  /**
   * 发送绑定邮箱所需的验证码。
   *
   * @param sendEmailCodeDTO 发送参数，不允许为 null；`email` 应为有效邮箱地址。
   * @param request          当前 HTTP 请求，允许为 null；主要用于提取真实请求来源 IP 以支撑风控与审计。
   * @throws RuntimeException 当当前用户未登录、邮箱已被占用、发送频率过高或邮件服务失败时抛出。
   * @implNote 该方法会写入消息日志和验证码记录，并发起邮件远程调用。
   */
  void sendBindEmailCode(SendEmailCodeDTO sendEmailCodeDTO, HttpServletRequest request);

  /**
   * 校验验证码并完成邮箱绑定。
   *
   * @param bindEmailDTO 绑定参数，不允许为 null；`email` 和 `code` 都必须有值。
   * @throws RuntimeException 当当前用户未登录、邮箱已被占用、验证码不存在、已过期、错误或用户不存在时抛出。
   * @implNote 该方法会更新用户邮箱，并修改验证码状态和失败次数。
   */
  void bindEmail(BindEmailDTO bindEmailDTO);

  /**
   * 基于旧密码修改当前账号密码。
   *
   * @param changePasswordDTO 改密参数，不允许为 null；新旧密码均不能为空。
   * @throws RuntimeException 当当前用户未登录、账号未绑定手机号/邮箱、旧密码错误、新密码不合法或用户不存在时抛出。
   * @implNote 该方法会更新用户密码密文，不会保留历史密码。
   */
  void changePassword(ChangePasswordDTO changePasswordDTO);

  /**
   * 为尚未设置过密码的账号设置初始密码。
   *
   * @param setPasswordDTO 设置参数，不允许为 null；新密码与确认密码都必须提供。
   * @throws RuntimeException 当当前用户未登录、账号未绑定手机号/邮箱、密码不一致、长度不足、账号已存在密码或用户不存在时抛出。
   * @implNote 该方法会修改用户密码密文；设置成功后账号将多出一种本地认证方式。
   */
  void setPassword(SetPasswordDTO setPasswordDTO);

  /**
   * 注销当前账号。
   *
   * @throws RuntimeException 当当前用户未登录、账号状态异常或用户不存在时抛出。
   * @implNote 该方法会修改用户状态为“已注销”，但不会删除数据库记录；未来可能引入数据清理或匿名化流程。
   */
  void cancelAccount();

  /**
   * 获取验证码图片和关联 Key。
   * 
   * @param request 当前 HTTP 请求，允许为 null；主要用于提取真实请求来源 IP 以支撑风控与审计。
   * @return 验证码信息对象，包含验证码 Key 和 Base64 编码的图片字符串，不返回 null。
   * @throws RuntimeException 当生成验证码失败或请求过于频繁时抛出。
   * @implNote 该方法会生成验证码记录并发起图片生成逻辑，验证码 Key 用于后续校验时关联用户输入和验证码摘要。
   */
  CaptchaVO getCaptcha(HttpServletRequest request);

  /**
   * 使用账号密码登录。
   *
   * @param passwordLoginDTO 登录参数，不允许为 null；其中 `account` 可以是手机号或邮箱，`password`
   *                         必须非空。
   * @throws RuntimeException 当账号不存在、密码错误、验证码校验失败、账号被封禁或用户不存在时抛出。
   * @implNote 该方法会校验账号密码，并且需要校验验证码的正确性以防止暴力破解；成功登录后会返回用户信息和 JWT。
   * @return 登录结果，不返回 null。
   */
  LoginUserVO passwordLogin(PasswordLoginDTO passwordLoginDTO);

  /**
   * 使用验证码登录。
   * 
   * @param codeLoginDTO 登录参数，不允许为 null；其中 `account` 可以是手机号或邮箱，`code`
   *                     必须非空且与最近发送的验证码匹配。
   * @throws RuntimeException 当账号不存在、验证码无效、过期或错误、账号被封禁或用户不存在时抛出。
   * @implNote 该方法会校验账号和验证码的正确性；成功登录后会返回用户信息和
   *           JWT。该登录方式适用于用户忘记密码但已绑定手机号/邮箱的场景，或者作为无密码登录的补充方式。
   * @return 登录结果，不返回 null。
   */
  LoginUserVO codeLogin(CodeLoginDTO codeLoginDTO);

  void sendLoginEmailCode(SendEmailCodeDTO sendEmailCodeDTO, HttpServletRequest request);

  void sendUnbindEmailCode(jakarta.servlet.http.HttpServletRequest request);
}
