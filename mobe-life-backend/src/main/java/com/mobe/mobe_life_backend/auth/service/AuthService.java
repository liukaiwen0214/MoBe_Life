/**
 * 文件级注释：
 * 核心职责：定义用户认证领域的业务逻辑契约，包括登录、Token 管理、账号绑定及密码操作。
 * 所属业务模块：认证授权中心 (Auth Center) - 业务逻辑层。
 * 重要依赖：
 * - 实现类需处理与微信开放平台、腾讯云 SES 的远程调用
 * - 依赖 JWT 工具类生成和验证 Token
 * - 依赖 Redis 实现验证码存储和限流
 */
package com.mobe.mobe_life_backend.auth.service;

import com.mobe.mobe_life_backend.auth.dto.BindEmailDTO;
import com.mobe.mobe_life_backend.auth.dto.BindPhoneDTO;
import com.mobe.mobe_life_backend.auth.dto.ChangePasswordDTO;
import com.mobe.mobe_life_backend.auth.dto.SendEmailCodeDTO;
import com.mobe.mobe_life_backend.auth.dto.SetPasswordDTO;
import com.mobe.mobe_life_backend.auth.dto.WxMiniLoginDTO;
import com.mobe.mobe_life_backend.auth.vo.LoginUserVO;
import com.mobe.mobe_life_backend.auth.vo.TokenVO;

/**
 * 认证服务接口，定义用户身份全生命周期管理的业务契约。
 *
 * <p>设计初衷：遵循接口隔离原则（ISP），将认证相关操作集中定义，
 * 便于多实现（如未来支持手机号验证码登录、第三方 OAuth 登录）。</p>
 *
 * <p>在架构中的角色：业务逻辑层（Service Layer），
 * 作为领域模型的防腐层，隔离 Controller 与底层数据访问、外部服务调用。</p>
 *
 * <p>核心业务概念：
 * - 双 Token 机制：accessToken（短期，7 天）+ refreshToken（长期，30 天）
 * - 微信登录：基于 OAuth2.0 授权码模式，code 换取 openid/session_key
 * - 账号绑定：将微信 openid 与手机号、邮箱建立多对多关联</p>
 *
 * <p>线程安全性：实现类需保证线程安全，建议使用 Spring 单例模式管理。</p>
 *
 * <p>使用示例：
 * <pre>
 *   // 微信小程序登录
 *   WxMiniLoginDTO dto = new WxMiniLoginDTO();
 *   dto.setCode(wxCode);
 *   LoginUserVO user = authService.wxMiniLogin(dto);
 *   String accessToken = user.getToken().getAccessToken();
 * </pre></p>
 */
public interface AuthService {

  /**
   * 微信小程序登录。
   *
   * <p>业务逻辑：通过微信临时 code 换取用户唯一标识 openid，
   * 若用户首次登录则自动注册，返回用户信息和双 Token。</p>
   *
   * @param wxMiniLoginDTO 登录参数，包含微信 code 和可选用户信息
   *                       - 约束：code 非空，有效期 5 分钟
   *                       - 允许值：标准微信登录凭证
   *
   * @return LoginUserVO 登录结果，永不返回 null
   *         - 包含用户信息（id、nickname、avatar 等）
   *         - 包含双 Token（accessToken、refreshToken）
   *
   * <p>异常说明：
   * - 微信 code 无效：BusinessException，message="微信登录失败"
   * - 微信服务器异常：BusinessException，message="微信服务暂不可用"</p>
   *
   * <p>副作用：
   * 1. 可能创建新用户记录（首次登录）
   * 2. 发起微信 code2session API 调用
   * 3. 生成并返回 JWT Token 对</p>
   */
  LoginUserVO wxMiniLogin(WxMiniLoginDTO wxMiniLoginDTO);

  /**
   * 刷新访问令牌。
   *
   * <p>业务逻辑：验证 refreshToken 有效性，生成新的 accessToken 和 refreshToken，
   * 采用双 Token 刷新策略增强安全性。</p>
   *
   * @param authorization HTTP Authorization Header，格式 "Bearer {refreshToken}"
   *                      - 允许值：有效的 refreshToken（JWT 格式）
   *                      - 约束：Token 未过期且未被吊销
   *
   * @return TokenVO 新的 Token 对
   *         - accessToken：新的短期访问令牌
   *         - refreshToken：新的长期刷新令牌（旧 Token 立即失效）
   *
   * <p>异常说明：
   * - Token 格式错误：BusinessException，code=401
   * - Token 已过期：BusinessException，code=401
   * - Token 已被吊销：BusinessException，code=401</p>
   *
   * <p>副作用：
   * 1. 解析并验证 refreshToken
   * 2. 生成新的 JWT Token 对
   * 3. 可选：将旧 refreshToken 加入黑名单</p>
   */
  TokenVO refreshToken(String authorization);

  /**
   * 用户登出。
   *
   * <p>业务逻辑：清理用户当前会话状态，将当前 Token 加入黑名单（若启用）。</p>
   *
   * <p>异常说明：无异常抛出，幂等操作。</p>
   *
   * <p>副作用：
   * 1. 将当前 accessToken 加入黑名单（TTL 设为 Token 剩余有效期）
   * 2. 清理 ThreadLocal 用户上下文</p>
   */
  void logout();

  /**
   * 绑定手机号。
   *
   * <p>业务逻辑：解密微信加密数据获取明文手机号，绑定到当前登录用户。</p>
   *
   * @param bindPhoneDTO 绑定参数，包含微信加密数据
   *                     - encryptedData：微信加密手机号数据
   *                     - iv：加密算法的初始向量
   *                     - 约束：数据需在微信小程序环境中获取，有效期较短
   *
   * <p>异常说明：
   * - 解密失败：BusinessException，message="手机号获取失败，请重试"
   * - 手机号已被绑定：BusinessException，code=409
   * - 非微信环境数据：BusinessException，code=400</p>
   *
   * <p>副作用：
   * 1. 调用微信解密接口（使用 session_key）
   * 2. 更新用户手机号字段
   * 3. 可能触发账号合并（若该手机号已有其他微信绑定）</p>
   */
  void bindPhone(BindPhoneDTO bindPhoneDTO);

  /**
   * 发送邮箱绑定验证码。
   *
   * <p>业务逻辑：生成 6 位数字验证码，通过腾讯云 SES 发送至目标邮箱，
   * 验证码 5 分钟内有效，同一邮箱 60 秒内只能发送一次。</p>
   *
   * @param sendEmailCodeDTO 发送参数，包含目标邮箱
   *                         - email：有效的邮箱地址
   *                         - 约束：符合 RFC 5322 格式
   *
   * <p>异常说明：
   * - 邮箱格式无效：MethodArgumentNotValidException（由 @Valid 触发）
   * - 发送频率超限：BusinessException，code=429，message="发送过于频繁，请稍后再试"
   * - 邮件服务异常：BusinessException，code=503</p>
   *
   * <p>副作用：
   * 1. 生成 6 位随机验证码（纯数字，范围 000000-999999）
   * 2. 验证码写入 Redis，key=email:code:{email}，TTL=300s
   * 3. 记录发送频率到 Redis，key=email:limit:{email}，TTL=60s
   * 4. 调用腾讯云 SES 发送邮件</p>
   */
  void sendBindEmailCode(SendEmailCodeDTO sendEmailCodeDTO);

  /**
   * 绑定邮箱。
   *
   * <p>业务逻辑：验证用户输入的邮箱验证码，验证通过后绑定邮箱到当前用户账号。</p>
   *
   * @param bindEmailDTO 绑定参数，包含邮箱和验证码
   *                     - email：待绑定的邮箱地址
   *                     - code：用户收到的 6 位验证码
   *                     - 约束：验证码 5 分钟内有效，只能使用一次
   *
   * <p>异常说明：
   * - 验证码错误：BusinessException，message="验证码错误"
   * - 验证码过期：BusinessException，message="验证码已过期，请重新获取"
   * - 邮箱已被绑定：BusinessException，code=409</p>
   *
   * <p>副作用：
   * 1. 从 Redis 读取并比对验证码
   * 2. 验证成功后删除 Redis 中的验证码（一次性使用）
   * 3. 更新用户邮箱字段</p>
   */
  void bindEmail(BindEmailDTO bindEmailDTO);

  /**
   * 修改密码。
   *
   * <p>业务逻辑：验证原密码正确性，更新为新密码，
   * 修改后可选使当前所有 Token 失效（强制重新登录）。</p>
   *
   * @param changePasswordDTO 修改参数，包含原密码和新密码
   *                          - oldPassword：当前密码（明文）
   *                          - newPassword：新密码（明文），强度要求：6-20 位，含字母和数字
   *                          - 约束：新旧密码不能相同
   *
   * <p>异常说明：
   * - 原密码错误：BusinessException，message="原密码错误"
   * - 新旧密码相同：BusinessException，message="新密码不能与旧密码相同"
   * - 未设置过密码：BusinessException，code=403</p>
   *
   * <p>副作用：
   * 1. 使用 BCrypt 校验原密码
   * 2. 使用 BCrypt 加密新密码并更新数据库
   * 3. 可选：将用户所有 Token 加入黑名单</p>
   */
  void changePassword(ChangePasswordDTO changePasswordDTO);

  /**
   * 设置密码。
   *
   * <p>业务逻辑：为微信登录用户设置初始密码，设置后可通过邮箱+密码登录。</p>
   *
   * @param setPasswordDTO 设置参数，包含新密码
   *                       - password：新密码（明文）
   *                       - 约束：6-20 位，必须同时包含字母和数字
   *                       - 业务规则：用户必须先绑定邮箱才能设置密码
   *
   * <p>异常说明：
   * - 密码强度不足：MethodArgumentNotValidException
   * - 未绑定邮箱：BusinessException，code=403，message="请先绑定邮箱"
   * - 已设置过密码：BusinessException，message="请使用修改密码功能"</p>
   *
   * <p>副作用：
   * 1. 使用 BCrypt（强度 10）加密密码
   * 2. 更新用户密码字段
   * 3. 启用邮箱+密码登录能力</p>
   */
  void setPassword(SetPasswordDTO setPasswordDTO);
}
