package com.mobe.mobe_life_backend.auth.controller;

import com.mobe.mobe_life_backend.auth.dto.BindPhoneDTO;
import com.mobe.mobe_life_backend.auth.dto.WxMiniLoginDTO;
import com.mobe.mobe_life_backend.auth.service.AuthService;
import com.mobe.mobe_life_backend.auth.vo.LoginUserVO;
import com.mobe.mobe_life_backend.auth.vo.TokenVO;
import com.mobe.mobe_life_backend.common.response.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/wx-mini-login")
  public Result<LoginUserVO> wxMiniLogin(@RequestBody @Valid WxMiniLoginDTO wxMiniLoginDTO) {
    return Result.success(authService.wxMiniLogin(wxMiniLoginDTO));
  }

  @GetMapping("/refresh-token")
  public Result<TokenVO> refreshToken(@RequestHeader("Authorization") String authorization) {
    return Result.success(authService.refreshToken(authorization));
  }

  @PostMapping("/logout")
  public Result<Boolean> logout() {
    authService.logout();
    return Result.success(true);
  }

  @PostMapping("/bind-phone")
  public Result<Boolean> bindPhone(@RequestBody @Valid BindPhoneDTO bindPhoneDTO) {
    authService.bindPhone(bindPhoneDTO);
    return Result.success(true);
  }
}