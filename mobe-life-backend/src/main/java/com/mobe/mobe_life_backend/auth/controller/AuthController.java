/**
 * 文件级注释：
 * 核心职责：处理用户认证相关的 HTTP 请求，包括微信小程序登录、Token 刷新、账号绑定及密码管理。
 * 所属业务模块：认证授权中心 (Auth Center)。
 * 重要依赖：
 * - AuthService：业务逻辑层接口，处理具体的认证流程
 * - JWT 机制：无状态会话管理，支持 Token 刷新机制
 * - 微信小程序 OAuth：与微信开放平台对接获取用户身份
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器，处理所有与用户身份认证相关的 HTTP 端点。
 *
 * <p>设计初衷：作为认证模块的 RESTful API 入口，采用无状态设计（Stateless），
 * 所有会话信息通过 JWT Token 传递，便于水平扩展和微服务拆分。</p>
 *
 * <p>在架构中的角色：表现层（Presentation Layer），职责单一：
 * 1. 接收并校验 HTTP 请求参数（DTO 校验）
 * 2. 调用 Service 层执行业务逻辑
 * 3. 将业务结果封装为统一响应格式（Result）</p>
 *
 * <p>线程安全性：Spring 默认以单例模式管理 Controller，
 * 本类通过 final 字段注入依赖，无实例变量修改操作，线程安全。</p>
 *
 * <p>安全设计：
 * - 除 /wx-mini-login 和 /refresh-token 外，其他端点需携带有效 JWT
 * - 敏感操作（改密、绑定）需二次校验当前用户身份
 * - 验证码防刷由 Service 层通过 Redis 限流控制</p>
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  /**
   * 认证服务接口，处理核心业务逻辑。
   * 注入方式：构造器注入（@RequiredArgsConstructor 生成），
   * 优势：字段可声明为 final，确保依赖不可变，利于单元测试。
   */
  private final AuthService authService;

  /**
   * 微信小程序登录接口。
   *
   * <p>业务场景：用户首次打开小程序或本地 Token 过期时，
   * 通过 wx.login() 获取临时 code，换取后端长期身份凭证。</p>
   *
   * @param wxMiniLoginDTO 登录请求体，包含微信临时 code 和可选的用户信息
   *                       - code：微信登录凭证，有效期 5 分钟，一次性使用
   *                       - 约束：不允许为空，由 @Valid 触发校验
   *
   * @return Result<LoginUserVO> 登录成功返回用户信息及双 Token（accessToken + refreshToken）
   *         - data 永不为 null，登录失败时抛业务异常，由全局异常处理器转换为错误响应
   *
   * <p>副作用：
   * 1. 若用户首次登录，自动创建用户记录（openid 关联）
   * 2. 发起微信服务器远程调用（code2session）
   * 3. 可能生成新的 JWT Token 对</p>
   *
   * <p>异常场景：
   * - 微信 code 无效或过期：抛出 BusinessException，code=400
   * - 微信服务器不可用：抛出 BusinessException，code=503</p>
   */
  @PostMapping("/wx-mini-login")
  public Result<LoginUserVO> wxMiniLogin(@RequestBody @Valid WxMiniLoginDTO wxMiniLoginDTO) {
    return Result.success(authService.wxMiniLogin(wxMiniLoginDTO));
  }

  /**
   * Token 刷新接口。
   *
   * <p>业务场景：accessToken 临近过期（通常 7 天）时，
   * 客户端使用 refreshToken 换取新的 accessToken，实现无感知续期。</p>
   *
   * @param authorization HTTP Header 中的 Authorization，格式：Bearer {refreshToken}
   *                      - 允许值：以 "Bearer " 开头的有效 refreshToken
   *                      - 约束：refreshToken 本身也是 JWT，但具有更长有效期
   *
   * @return Result<TokenVO> 新的 accessToken 和 refreshToken（双 Token 刷新策略）
   *         - 返回全新 Token 对，旧 refreshToken 立即失效（防重放）
   *
   * <p>副作用：
   * 1. 验证 refreshToken 有效性及未过期
   * 2. 生成新的 JWT Token 对
   * 3. 旧 Token 加入黑名单（若实现 Token 吊销机制）</p>
   *
   * <p>异常场景：
   * - Token 格式错误或已过期：抛出 BusinessException，code=401</p>
   */
  @GetMapping("/refresh-token")
  public Result<TokenVO> refreshToken(@RequestHeader("Authorization") String authorization) {
    return Result.success(authService.refreshToken(authorization));
  }

  /**
   * 用户登出接口。
   *
   * <p>业务场景：用户主动退出登录，或小程序被微信回收时调用，
   * 清理服务端会话状态（尽管 JWT 本身无状态，但可实现 Token 黑名单）。</p>
   *
   * @return Result<Boolean> 固定返回 true，表示操作已受理
   *         - 即使 Token 已过期也返回成功（幂等设计）
   *
   * <p>副作用：
   * 1. 将当前 Token 加入黑名单（若启用 Token 吊销）
   * 2. 清理 ThreadLocal 中的用户上下文（由拦截器 afterCompletion 处理）</p>
   *
   * <p>前置条件：需携带有效 JWT（已通过 JwtInterceptor 校验）</p>
   */
  @PostMapping("/logout")
  public Result<Boolean> logout() {
    authService.logout();
    return Result.success(true);
  }

  /**
   * 绑定手机号接口。
   *
   * <p>业务场景：微信小程序用户通过 getPhoneNumber 组件获取加密手机号数据，
   * 解密后绑定到当前用户账号，用于后续短信通知或账号找回。</p>
   *
   * @param bindPhoneDTO 绑定请求体，包含微信加密手机号数据
   *                     - encryptedData、iv：微信加密参数
   *                     - 约束：需在微信小程序环境中获取
   *
   * <p>副作用：
   * 1. 调用微信解密接口获取明文手机号
   * 2. 更新 mobe_user 表的 phone 字段
   * 3. 若该手机号已被其他账号绑定，可能触发账号合并逻辑</p>
   *
   * <p>异常场景：
   * - 微信加密数据解密失败：抛出 BusinessException，code=400
   * - 手机号已被绑定：抛出 BusinessException，code=409（冲突）</p>
   */
  @PostMapping("/bind-phone")
  public Result<Boolean> bindPhone(@RequestBody @Valid BindPhoneDTO bindPhoneDTO) {
    authService.bindPhone(bindPhoneDTO);
    return Result.success(true);
  }

  /**
   * 发送邮箱验证码接口。
   *
   * <p>业务场景：用户绑定邮箱前，需验证邮箱所有权，
   * 系统生成 6 位数字验证码并通过腾讯云 SES 发送。</p>
   *
   * @param sendEmailCodeDTO 发送请求体，包含目标邮箱地址
   *                         - email：需符合 RFC 5322 邮箱格式
   *                         - 防刷限制：同一邮箱 60 秒内只能发送一次
   *
   * <p>副作用：
   * 1. 生成 6 位随机验证码（数字）
   * 2. 验证码存入 Redis，TTL=300 秒（5 分钟有效）
   * 3. 调用腾讯云 SES 发送邮件（异步或同步取决于配置）</p>
   *
   * <p>异常场景：
   * - 邮箱格式无效：由 @Valid 触发 MethodArgumentNotValidException
   * - 发送频率超限：抛出 BusinessException，code=429（限流）</p>
   */
  @PostMapping("/send-email-code")
  public Result<Boolean> sendEmailCode(@RequestBody @Valid SendEmailCodeDTO sendEmailCodeDTO) {
    authService.sendBindEmailCode(sendEmailCodeDTO);
    return Result.success(true);
  }

  /**
   * 绑定邮箱接口。
   *
   * <p>业务场景：用户输入收到的邮箱验证码，验证通过后绑定邮箱，
   * 绑定后可用于邮箱登录、密码找回、接收系统通知。</p>
   *
   * @param bindEmailDTO 绑定请求体，包含邮箱和验证码
   *                     - email：待绑定的邮箱地址
   *                     - code：用户收到的 6 位验证码
   *
   * <p>副作用：
   * 1. 校验验证码正确性和时效性（Redis 比对）
   * 2. 更新 mobe_user 表的 email 字段
   * 3. 删除已使用的验证码（防重放攻击）</p>
   *
   * <p>异常场景：
   * - 验证码错误或过期：抛出 BusinessException，code=400
   * - 邮箱已被其他账号绑定：抛出 BusinessException，code=409</p>
   */
  @PostMapping("/bind-email")
  public Result<Boolean> bindEmail(@RequestBody @Valid BindEmailDTO bindEmailDTO) {
    authService.bindEmail(bindEmailDTO);
    return Result.success(true);
  }

  /**
   * 设置密码接口。
   *
   * <p>业务场景：微信一键登录的用户初始无密码，
   * 绑定邮箱后可通过本接口设置密码，实现邮箱+密码登录。</p>
   *
   * @param setPasswordDTO 设置请求体，包含新密码
   *                       - password：明文密码，长度 6-20 位，需包含字母和数字
   *                       - 安全处理：Controller 层接收明文，Service 层使用 BCrypt 加密存储
   *
   * <p>副作用：
   * 1. 使用 BCrypt 算法（强度 10）对密码进行哈希
   * 2. 更新 mobe_user 表的 password 字段
   * 3. 设置密码后，用户可使用邮箱+密码组合登录</p>
   *
   * <p>前置条件：用户已绑定邮箱（业务规则，非技术约束）</p>
   *
   * <p>异常场景：
   * - 密码强度不足：由 @Valid 触发校验异常
   * - 未绑定邮箱：抛出 BusinessException，code=403</p>
   */
  @PostMapping("/set-password")
  public Result<Boolean> setPassword(@RequestBody @Valid SetPasswordDTO setPasswordDTO) {
    authService.setPassword(setPasswordDTO);
    return Result.success(true);
  }

  /**
   * 修改密码接口。
   *
   * <p>业务场景：已设置密码的用户主动修改密码，
   * 需验证原密码以防止账号被盗后的恶意修改。</p>
   *
   * @param changePasswordDTO 修改请求体，包含原密码和新密码
   *                          - oldPassword：当前使用的密码（明文）
   *                          - newPassword：新密码（明文），强度要求同设置密码
   *
   * <p>副作用：
   * 1. 使用 BCrypt 校验原密码正确性
   * 2. 使用 BCrypt 加密新密码并更新数据库
   * 3. 可选：使当前所有 Token 失效，强制重新登录（安全增强）</p>
   *
   * <p>异常场景：
   * - 原密码错误：抛出 BusinessException，code=400
   * - 新密码与原密码相同：抛出 BusinessException，code=400</p>
   */
  @PostMapping("/change-password")
  public Result<Boolean> changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
    authService.changePassword(changePasswordDTO);
    return Result.success(true);
  }
}
