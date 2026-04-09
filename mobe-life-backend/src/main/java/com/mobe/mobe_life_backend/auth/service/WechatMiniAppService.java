/**
 * 核心职责：抽象微信小程序相关远程能力，例如登录换取会话和获取手机号。
 * 所属业务模块：认证中心 / 第三方平台服务抽象。
 * 重要依赖关系或外部约束：实现类依赖微信开放接口，必须严格遵循微信接口的时效性与错误码语义。
 */
package com.mobe.mobe_life_backend.auth.service;

import com.mobe.mobe_life_backend.auth.vo.WxCode2SessionVO;
import com.mobe.mobe_life_backend.auth.vo.WxPhoneNumberVO;

/**
 * 微信小程序服务接口。
 *
 * <p>设计初衷是把微信平台的协议差异收口在单独服务里，
 * 避免认证主流程散落着 URL 拼装、错误码解释和 JSON 解析逻辑。</p>
 *
 * <p>线程安全性：实现类一般作为无状态单例 Bean 使用，调用方无需自行做同步。</p>
 */
public interface WechatMiniAppService {

  /**
   * 使用微信临时登录凭证换取会话信息。
   *
   * @param code 微信 `wx.login` 返回的临时凭证，不允许为空；通常只能使用一次，且有效期较短。
   * @return 微信会话结果，不返回 null；成功时至少包含 `openid`，失败时实现类应直接抛异常而不是返回空对象。
   * @throws RuntimeException 当微信接口返回错误码、网络失败或响应中缺失 `openid` 时抛出。
   * @implNote 该方法会发起远程 GET 请求到微信服务端。
   */
  WxCode2SessionVO code2Session(String code);

  /**
   * 使用微信手机号场景凭证获取用户手机号。
   *
   * @param code 微信手机号授权场景返回的临时凭证，不允许为空；重复使用或过期会被微信拒绝。
   * @return 手机号信息，不返回 null；成功时至少应包含 `purePhoneNumber`。
   * @throws RuntimeException 当 access_token 获取失败、手机号接口返回错误码或响应缺失 `phone_info` 时抛出。
   * @implNote 该方法会串行发起两次远程调用：先换取 access_token，再换取手机号。
   */
  WxPhoneNumberVO getPhoneNumber(String code);
}
