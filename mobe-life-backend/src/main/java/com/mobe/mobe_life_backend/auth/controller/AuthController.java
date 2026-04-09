/**
 * 核心职责：暴露认证中心对外 HTTP 接口，负责参数校验、业务委派和统一响应封装。
 * 所属业务模块：认证中心 / 表现层。
 * 重要依赖关系或外部约束：依赖 `AuthService` 执行业务；除登录和 token 刷新外，其余接口默认受 JWT 拦截器保护。
 */
package com.mobe.mobe_life_backend.auth.controller;

import com.mobe.mobe_life_backend.auth.dto.BindEmailDTO;
import com.mobe.mobe_life_backend.auth.dto.BindPhoneDTO;
import com.mobe.mobe_life_backend.auth.dto.ChangePasswordDTO;
import com.mobe.mobe_life_backend.auth.dto.SendEmailCodeDTO;
import com.mobe.mobe_life_backend.auth.dto.SetPasswordDTO;
import com.mobe.mobe_life_backend.auth.dto.WxMiniLoginDTO;
import com.mobe.mobe_life_backend.auth.service.AuthService;
import com.mobe.mobe_life_backend.auth.vo.LoginUserVO;
import com.mobe.mobe_life_backend.auth.vo.TokenVO;
import com.mobe.mobe_life_backend.common.response.Result;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器。
 *
 * <p>
 * 设计初衷是把与“身份建立”和“账号安全增强”相关的接口集中收口，保证客户端在认证域中有清晰稳定的 API 入口。
 * </p>
 *
 * <p>
 * 线程安全性：Controller 由 Spring 以单例方式管理；本类仅持有不可变依赖，不保存请求级状态，线程安全。
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  /**
   * 认证服务。
   * 控制层只负责转发经过校验的请求，避免在这里写业务判断。
   */
  private final AuthService authService;

  /**
   * 微信小程序登录。
   *
   * @param wxMiniLoginDTO 请求体，不允许为 null；`code` 必须是有效微信临时登录凭证。
   * @return 登录结果，不返回 null；成功后返回用户基本信息与 JWT。
   * @throws RuntimeException 当参数校验失败或底层认证流程失败时抛出，由全局异常处理器统一转成响应。
   * @implNote 该接口可能触发新用户建档，并会发起微信远程调用。
   */
  @PostMapping("/wx-mini-login")
  public Result<LoginUserVO> wxMiniLogin(@RequestBody @Valid WxMiniLoginDTO wxMiniLoginDTO) {
    return Result.success(authService.wxMiniLogin(wxMiniLoginDTO));
  }

  /**
   * 刷新 JWT。
   *
   * @param authorization `Authorization` 请求头，不允许为空；支持带 `Bearer ` 前缀。
   * @return 新 token 结果，不返回 null。
   * @throws RuntimeException 当 token 无效或已过期时抛出。
   * @implNote 当前实现直接基于旧 token 解析用户 ID 并重新签发新 token。
   */
  @GetMapping("/refresh-token")
  public Result<TokenVO> refreshToken(@RequestHeader("Authorization") String authorization) {
    return Result.success(authService.refreshToken(authorization));
  }

  /**
   * 用户主动登出。
   *
   * @return 固定返回 `true`；当前实现是幂等的。
   * @implNote 由于当前采用无状态 JWT，服务端没有额外清理动作。
   */
  @PostMapping("/logout")
  public Result<Boolean> logout() {
    authService.logout();
    return Result.success(true);
  }

  /**
   * 绑定手机号。
   *
   * @param bindPhoneDTO 请求体，不允许为 null；`code` 必须来自微信手机号授权场景。
   * @return 固定返回 `true`，表示绑定动作成功完成。
   * @throws RuntimeException 当当前用户未登录、微信接口失败、手机号冲突或参数非法时抛出。
   * @implNote 该接口会更新当前用户资料。
   */
  @PostMapping("/bind-phone")
  public Result<Boolean> bindPhone(@RequestBody @Valid BindPhoneDTO bindPhoneDTO) {
    authService.bindPhone(bindPhoneDTO);
    return Result.success(true);
  }

  /**
   * 发送绑定邮箱验证码。
   *
   * @param sendEmailCodeDTO 请求体，不允许为 null；`email` 必须通过格式校验。
   * @param request          当前 HTTP 请求，不允许为 null；服务层会尝试提取真实来源 IP。
   * @return 固定返回 `true`，表示发送请求已成功受理。
   * @throws RuntimeException 当邮箱已被占用、发送过于频繁或邮件服务失败时抛出。
   * @implNote 该接口会写消息日志、验证码表，并发起邮件远程调用。
   */
  @PostMapping("/send-email-code")
  public Result<Boolean> sendEmailCode(@RequestBody @Valid SendEmailCodeDTO sendEmailCodeDTO,
      HttpServletRequest request) {
    authService.sendBindEmailCode(sendEmailCodeDTO, request);
    return Result.success(true);
  }

  /**
   * 校验验证码并绑定邮箱。
   *
   * @param bindEmailDTO 请求体，不允许为 null；邮箱和验证码都必须提供。
   * @return 固定返回 `true`，表示绑定成功。
   * @throws RuntimeException 当邮箱冲突、验证码无效或当前用户状态异常时抛出。
   * @implNote 该接口会修改用户邮箱字段和验证码状态。
   */
  @PostMapping("/bind-email")
  public Result<Boolean> bindEmail(@RequestBody @Valid BindEmailDTO bindEmailDTO) {
    authService.bindEmail(bindEmailDTO);
    return Result.success(true);
  }

  /**
   * 为当前账号设置初始密码。
   *
   * @param setPasswordDTO 请求体，不允许为 null；两次密码输入必须在服务层通过一致性校验。
   * @return 固定返回 `true`，表示密码已成功设置。
   * @throws RuntimeException 当账号已存在密码、密码不一致、长度不足或用户未满足绑定前置条件时抛出。
   * @implNote 该接口会更新数据库中的密码密文。
   */
  @PostMapping("/set-password")
  public Result<Boolean> setPassword(@RequestBody @Valid SetPasswordDTO setPasswordDTO) {
    authService.setPassword(setPasswordDTO);
    return Result.success(true);
  }

  /**
   * 修改当前账号密码。
   *
   * @param changePasswordDTO 请求体，不允许为 null；必须同时提供旧密码和新密码。
   * @return 固定返回 `true`，表示密码已成功更新。
   * @throws RuntimeException 当旧密码错误、新密码不合法或账号状态不满足改密前置条件时抛出。
   * @implNote 该接口会更新数据库中的密码密文，但当前不会主动使既有 JWT 失效。
   */
  @PostMapping("/change-password")
  public Result<Boolean> changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
    authService.changePassword(changePasswordDTO);
    return Result.success(true);
  }

  /**
   * 注销账号。
   *
   * @return 固定返回 `true`，表示账号已成功标记为注销状态。
   * @throws RuntimeException 当当前用户未登录、账号状态异常或用户不存在时抛出。
   * @implNote 该接口会修改用户状态为“已注销”，但不会删除数据库记录；未来可能引入数据清理或匿名化流程。
   */
  @PostMapping("/cancel-account")
  public Result<Boolean> cancelAccount() {
    authService.cancelAccount();
    return Result.success(true);
  }
}
