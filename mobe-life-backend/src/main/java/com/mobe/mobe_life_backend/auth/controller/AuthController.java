package com.mobe.mobe_life_backend.auth.controller;

import com.mobe.mobe_life_backend.auth.dto.BindEmailDTO;
import com.mobe.mobe_life_backend.auth.dto.BindPhoneDTO;
import com.mobe.mobe_life_backend.auth.dto.ChangePasswordDTO;
import com.mobe.mobe_life_backend.auth.dto.CodeLoginDTO;
import com.mobe.mobe_life_backend.auth.dto.PasswordLoginDTO;
import com.mobe.mobe_life_backend.auth.dto.SendEmailCodeDTO;
import com.mobe.mobe_life_backend.auth.dto.SetPasswordDTO;
import com.mobe.mobe_life_backend.auth.dto.WxMiniLoginDTO;
import com.mobe.mobe_life_backend.auth.service.AuthService;
import com.mobe.mobe_life_backend.auth.vo.CaptchaVO;
import com.mobe.mobe_life_backend.auth.vo.LoginUserVO;
import com.mobe.mobe_life_backend.auth.vo.TokenVO;
import com.mobe.mobe_life_backend.common.response.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证模块接口", description = "提供登录认证、账号绑定、密码管理与验证码相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "微信小程序登录", description = "通过微信小程序临时登录凭证完成登录，首次登录会自动注册账号并返回用户信息与访问令牌")
  @PostMapping("/wx-mini-login")
  public Result<LoginUserVO> wxMiniLogin(
      @RequestBody @Valid WxMiniLoginDTO wxMiniLoginDTO) {
    return Result.success(authService.wxMiniLogin(wxMiniLoginDTO));
  }

  @Operation(summary = "刷新访问令牌", description = "根据当前请求头中的旧 token 签发新的访问令牌")
  @GetMapping("/refresh-token")
  public Result<TokenVO> refreshToken(
      @Parameter(description = "登录访问令牌，请求头中传入 Bearer Token", required = true, example = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.signature") @RequestHeader("Authorization") String authorization) {
    return Result.success(authService.refreshToken(authorization));
  }

  @Operation(summary = "退出登录", description = "当前登录用户主动退出登录，当前无状态 token 模式下主要由客户端清理本地登录态")
  @PostMapping("/logout")
  public Result<Boolean> logout() {
    authService.logout();
    return Result.success(true);
  }

  @Operation(summary = "绑定手机号", description = "通过微信手机号授权凭证为当前登录账号绑定手机号")
  @PostMapping("/bind-phone")
  public Result<Boolean> bindPhone(
      @RequestBody @Valid BindPhoneDTO bindPhoneDTO) {
    authService.bindPhone(bindPhoneDTO);
    return Result.success(true);
  }

  @Operation(summary = "发送绑定邮箱验证码", description = "向待绑定邮箱发送验证码，用于后续邮箱绑定校验")
  @PostMapping("/send-email-code")
  public Result<Boolean> sendEmailCode(
      @RequestBody @Valid SendEmailCodeDTO sendEmailCodeDTO,
      HttpServletRequest request) {
    authService.sendBindEmailCode(sendEmailCodeDTO, request);
    return Result.success(true);
  }

  @Operation(summary = "绑定邮箱", description = "校验邮箱验证码并将邮箱绑定到当前登录账号")
  @PostMapping("/bind-email")
  public Result<Boolean> bindEmail(
      @RequestBody @Valid BindEmailDTO bindEmailDTO) {
    authService.bindEmail(bindEmailDTO);
    return Result.success(true);
  }

  @Operation(summary = "设置登录密码", description = "为当前尚未设置密码的账号设置初始登录密码")
  @PostMapping("/set-password")
  public Result<Boolean> setPassword(
      @RequestBody @Valid SetPasswordDTO setPasswordDTO) {
    authService.setPassword(setPasswordDTO);
    return Result.success(true);
  }

  @Operation(summary = "修改登录密码", description = "校验旧密码后更新当前账号的登录密码")
  @PostMapping("/change-password")
  public Result<Boolean> changePassword(
      @RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
    authService.changePassword(changePasswordDTO);
    return Result.success(true);
  }

  @Operation(summary = "注销账号", description = "将当前登录账号执行注销处理")
  @PostMapping("/cancel-account")
  public Result<Boolean> cancelAccount() {
    authService.cancelAccount();
    return Result.success(true);
  }

  @Operation(summary = "获取图形验证码", description = "生成登录所需的图形验证码标识与 Base64 图片内容")
  @GetMapping("/captcha")
  public Result<CaptchaVO> getCaptcha(HttpServletRequest request) {
    return Result.success(authService.getCaptcha(request));
  }

  @Operation(summary = "账号密码登录", description = "使用手机号或邮箱配合密码及图形验证码完成登录")
  @PostMapping("/password-login")
  public Result<LoginUserVO> passwordLogin(
      @RequestBody @Valid PasswordLoginDTO passwordLoginDTO) {
    return Result.success(authService.passwordLogin(passwordLoginDTO));
  }

  @Operation(summary = "验证码登录", description = "使用手机号或邮箱配合邮箱验证码完成快捷登录")
  @PostMapping("/code-login")
  public Result<LoginUserVO> codeLogin(
      @RequestBody @Valid CodeLoginDTO codeLoginDTO) {
    return Result.success(authService.codeLogin(codeLoginDTO));
  }

  @Operation(summary = "发送登录邮箱验证码", description = "向登录账号对应邮箱发送验证码，用于验证码登录场景")
  @PostMapping("/send-login-email-code")
  public Result<Boolean> sendLoginEmailCode(
      @RequestBody @Valid SendEmailCodeDTO sendEmailCodeDTO,
      HttpServletRequest request) {
    authService.sendLoginEmailCode(sendEmailCodeDTO, request);
    return Result.success(true);
  }

  @Operation(summary = "发送解绑邮箱验证码", description = "向当前账号已绑定邮箱发送验证码，用于邮箱解绑确认")
  @PostMapping("/send-unbind-email-code")
  public Result<Boolean> sendUnbindEmailCode(HttpServletRequest request) {
    authService.sendUnbindEmailCode(request);
    return Result.success(true);
  }
}
