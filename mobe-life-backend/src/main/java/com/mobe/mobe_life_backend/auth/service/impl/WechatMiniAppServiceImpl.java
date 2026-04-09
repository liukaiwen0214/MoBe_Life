/**
 * 核心职责：封装微信小程序开放接口调用，包括登录换取会话和获取手机号。
 * 所属业务模块：认证中心 / 第三方平台集成实现。
 * 重要依赖关系或外部约束：依赖微信小程序官方接口与配置凭证，接口错误码语义必须按微信规则解释。
 */
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

/**
 * 微信小程序服务实现。
 *
 * <p>设计初衷是把所有微信 HTTP 调用细节都限制在这个类里，
 * 这样认证主流程可以围绕业务语义编排，而不是混杂 URL、JSON 和错误码判断。</p>
 *
 * <p>线程安全性：实现类无可变共享状态，适合作为 Spring 单例 Bean 使用。
 * 需要注意的是该类依赖外部网络，不保证调用时延稳定。</p>
 */
@Service
@RequiredArgsConstructor
public class WechatMiniAppServiceImpl implements WechatMiniAppService {

  /**
   * 微信小程序配置。
   * 提供 AppID 和 AppSecret，支撑所有微信侧鉴权调用。
   */
  private final WechatMiniAppProperties wechatMiniAppProperties;

  /**
   * 通过微信临时登录凭证换取会话。
   *
   * @param code 微信 `wx.login` 返回的临时凭证，不允许为空。
   * @return 会话结果，不返回 null；成功时 `openid` 一定有值。
   * @throws BusinessException 当微信返回错误码、网络调用失败或响应缺失 `openid` 时抛出。
   * @implNote 该方法会发起一次远程 GET 调用。
   */
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
      // openid 是后续建档和鉴权的锚点，缺失时不能继续走“弱成功”分支。
      throw new BusinessException("微信登录失败：openid为空");
    }

    return result;
  }

  /**
   * 通过微信手机号场景 code 获取手机号。
   *
   * @param code 微信手机号授权场景返回的临时凭证，不允许为空。
   * @return 手机号结果，不返回 null；成功时 `purePhoneNumber` 一般有值。
   * @throws BusinessException 当 access_token 获取失败、手机号接口失败或响应缺失 `phone_info` 时抛出。
   * @implNote 该方法会连续发起两次远程 POST 调用。
   */
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
      // 这里不默认为空对象，是为了避免把“微信接口异常”误判成“用户没有手机号”。
      throw new BusinessException("获取微信手机号失败：phone_info为空");
    }

    WxPhoneNumberVO result = new WxPhoneNumberVO();
    result.setPhoneNumber(phoneInfo.getStr("phoneNumber"));
    result.setPurePhoneNumber(phoneInfo.getStr("purePhoneNumber"));
    result.setCountryCode(phoneInfo.getStr("countryCode"));
    return result;
  }
}
