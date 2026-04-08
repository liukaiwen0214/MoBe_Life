package com.mobe.mobe_life_backend.auth.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.mobe.mobe_life_backend.auth.config.WechatMiniAppProperties;
import com.mobe.mobe_life_backend.auth.service.WechatMiniAppService;
import com.mobe.mobe_life_backend.auth.vo.WxCode2SessionVO;
import com.mobe.mobe_life_backend.auth.vo.WxPhoneNumberVO;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WechatMiniAppServiceImpl implements WechatMiniAppService {

  private final WechatMiniAppProperties wechatMiniAppProperties;

  @Override
  public WxCode2SessionVO code2Session(String code) {
    String url = "https://api.weixin.qq.com/sns/jscode2session"
        + "?appid=" + wechatMiniAppProperties.getAppId()
        + "&secret=" + wechatMiniAppProperties.getAppSecret()
        + "&js_code=" + code
        + "&grant_type=authorization_code";

    String response = HttpUtil.get(url);
    WxCode2SessionVO result = JSONUtil.toBean(response, WxCode2SessionVO.class);

    if (result.getErrcode() != null && result.getErrcode() != 0) {
      throw new BusinessException("微信登录失败：" + result.getErrmsg());
    }

    if (result.getOpenid() == null || result.getOpenid().isBlank()) {
      throw new BusinessException("微信登录失败：openid为空");
    }

    return result;
  }

  @Override
  public WxPhoneNumberVO getPhoneNumber(String code) {
    String accessTokenUrl = "https://api.weixin.qq.com/cgi-bin/stable_token";
    String tokenBody = "{"
        + "\"grant_type\":\"client_credential\","
        + "\"appid\":\"" + wechatMiniAppProperties.getAppId() + "\","
        + "\"secret\":\"" + wechatMiniAppProperties.getAppSecret() + "\""
        + "}";

    String tokenResp = HttpUtil.createPost(accessTokenUrl)
        .body(tokenBody)
        .execute()
        .body();

    cn.hutool.json.JSONObject tokenJson = JSONUtil.parseObj(tokenResp);
    Integer tokenErrCode = tokenJson.getInt("errcode");
    if (tokenErrCode != null && tokenErrCode != 0) {
      throw new BusinessException("获取微信access_token失败：" + tokenJson.getStr("errmsg"));
    }

    String accessToken = tokenJson.getStr("access_token");
    if (accessToken == null || accessToken.isBlank()) {
      throw new BusinessException("获取微信access_token失败");
    }

    String phoneUrl = "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken;
    String phoneBody = "{\"code\":\"" + code + "\"}";

    String phoneResp = HttpUtil.createPost(phoneUrl)
        .body(phoneBody)
        .execute()
        .body();

    cn.hutool.json.JSONObject phoneJson = JSONUtil.parseObj(phoneResp);
    Integer errCode = phoneJson.getInt("errcode");
    if (errCode != null && errCode != 0) {
      throw new BusinessException("获取微信手机号失败：" + phoneJson.getStr("errmsg"));
    }

    cn.hutool.json.JSONObject phoneInfo = phoneJson.getJSONObject("phone_info");
    if (phoneInfo == null) {
      throw new BusinessException("获取微信手机号失败：phone_info为空");
    }

    WxPhoneNumberVO result = new WxPhoneNumberVO();
    result.setPhoneNumber(phoneInfo.getStr("phoneNumber"));
    result.setPurePhoneNumber(phoneInfo.getStr("purePhoneNumber"));
    result.setCountryCode(phoneInfo.getStr("countryCode"));
    return result;
  }
}