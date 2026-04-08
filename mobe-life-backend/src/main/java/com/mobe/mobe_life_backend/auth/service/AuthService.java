package com.mobe.mobe_life_backend.auth.service;

import com.mobe.mobe_life_backend.auth.dto.BindPhoneDTO;
import com.mobe.mobe_life_backend.auth.dto.WxMiniLoginDTO;
import com.mobe.mobe_life_backend.auth.vo.LoginUserVO;
import com.mobe.mobe_life_backend.auth.vo.TokenVO;

public interface AuthService {

  LoginUserVO wxMiniLogin(WxMiniLoginDTO wxMiniLoginDTO);

  TokenVO refreshToken(String authorization);

  void logout();

  void bindPhone(BindPhoneDTO bindPhoneDTO);
}