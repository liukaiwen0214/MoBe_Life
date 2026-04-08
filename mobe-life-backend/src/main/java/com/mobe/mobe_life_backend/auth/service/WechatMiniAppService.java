package com.mobe.mobe_life_backend.auth.service;

import com.mobe.mobe_life_backend.auth.vo.WxCode2SessionVO;
import com.mobe.mobe_life_backend.auth.vo.WxPhoneNumberVO;

public interface WechatMiniAppService {

  WxCode2SessionVO code2Session(String code);

  WxPhoneNumberVO getPhoneNumber(String code);
}