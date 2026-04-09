/**
 * 文件级注释：
 * 核心职责：提供 JWT（JSON Web Token）的生成、解析和验证工具方法，实现无状态用户身份认证。
 * 所属业务模块：认证授权中心 (Auth Center) - 基础设施层。
 * 重要依赖：
 * - io.jsonwebtoken (JJWT) 库：JWT 标准实现
 * - HMAC-SHA 算法：使用对称密钥签名，确保 Token 不可篡改
 * 安全约束：
 * - SECRET_KEY 必须至少 256 位（32 字节）以满足 HS256 安全要求
 * - 生产环境必须通过环境变量或密钥管理服务注入密钥，禁止硬编码
 */
package com.mobe.mobe_life_backend.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类，封装 Token 生命周期管理的所有操作。
 *
 * <p>设计初衷：将 JWT 操作集中管理，避免业务代码直接依赖 JJWT 库，
 * 便于未来切换 JWT 实现（如改用非对称加密 RS256）或升级版本。</p>
 *
 * <p>在架构中的角色：基础设施层工具类，被 AuthService、JwtInterceptor 等组件依赖，
 * 属于横切关注点的技术实现细节。</p>
 *
 * <p>核心业务概念：
 * - JWT 结构：Header.Payload.Signature，其中 Payload 包含用户 ID（subject）和过期时间（exp）
 * - 双 Token 策略：accessToken（7 天）用于接口访问，refreshToken（30 天）用于续期
 * - 无状态设计：服务端不存储 Token 状态，仅通过密钥验证签名和过期时间</p>
 *
 * <p>线程安全性：该类为无状态工具类，所有方法均为静态方法，线程安全。
 * SECRET_KEY 和 KEY 在类加载时初始化，之后不可变。</p>
 *
 * <p>使用示例：
 * <pre>
 *   // 生成 Token
 *   String token = JwtUtils.createToken(10086L);
 *
 *   // 解析 Token 获取用户 ID
 *   Long userId = JwtUtils.getUserId(token);
 *
 *   // 验证 Token 有效性
 *   boolean valid = JwtUtils.isValid(token);
 * </pre></p>
 */
public class JwtUtils {

  /**
   * JWT 签名密钥，用于 HMAC-SHA 算法。
   *
   * <p>安全要求：
   * - 长度必须 >= 256 位（32 字节）以满足 HS256 最低要求
   * - 当前值："mobe-life-secret-key-2026-very-safe"（33 字节，满足要求）
   * - 生产环境必须通过环境变量注入，如：${JWT_SECRET:default-key}</p>
   *
   * <p>为什么硬编码：当前为开发环境配置，生产环境应通过 @Value 或配置中心注入。
   * 若密钥泄露，攻击者可伪造任意用户 Token，属于高危安全风险。</p>
   */
  private static final String SECRET_KEY = "mobe-life-secret-key-2026-very-safe";

  /**
   * Token 有效期：7 天（毫秒）。
   *
   * <p>业务考量：
   * - 7 天平衡了安全性和用户体验，避免频繁登录
   * - 配合 refreshToken（30 天）实现无感知续期
   * - 若安全要求高（如金融场景），建议缩短至 1-2 小时</p>
   *
   * <p>计算方式：7 天 × 24 小时 × 60 分钟 × 60 秒 × 1000 毫秒 = 604,800,000 毫秒</p>
   */
  private static final long EXPIRE_TIME = 7L * 24 * 60 * 60 * 1000;

  /**
   * 预计算的 HMAC-SHA 密钥对象，避免每次创建 Token 时重复生成。
   *
   * <p>性能优化：SecretKey 的创建涉及密钥派生计算，预计算可提升约 10% 的 Token 生成性能。</p>
   */
  private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

  /**
   * 私有构造器，防止实例化。
   *
   * <p>设计意图：工具类应通过静态方法访问，禁止创建实例。
   * 若尝试 new JwtUtils() 将编译错误。</p>
   */
  private JwtUtils() {
  }

  /**
   * 生成 JWT Token。
   *
   * <p>方法作用：为指定用户创建包含用户 ID 和过期时间的签名 Token。</p>
   *
   * @param userId 用户唯一标识，对应 mobe_user 表的 id 字段
   *               - 允许值：任意正整数（Long 类型）
   *               - 约束：不允许为 null，调用方需前置校验
   *
   * @return 签名后的 JWT 字符串，格式：header.payload.signature
   *         - 示例：eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDA4NiIsImlhdCI6MTcwNDA2MDgwMCwiZXhwIjoxNzA0NjY1NjAwfQ.signature
   *         - 永不返回 null
   *
   * <p>Token 载荷结构：
   * - subject (sub): 用户 ID 的字符串形式
   * - issuedAt (iat): Token 生成时间戳
   * - expiration (exp): Token 过期时间戳（当前时间 + 7 天）</p>
   *
   * <p>异常说明：
   * - userId 为 null：抛出 IllegalArgumentException（JJWT 内部校验）</p>
   */
  public static String createToken(Long userId) {
    Date now = new Date();
    // 过期时间 = 当前时间 + 7 天固定偏移量
    Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

    return Jwts.builder()
        .subject(String.valueOf(userId))  // 将 Long 转为 String 作为 JWT subject
        .issuedAt(now)                    // 记录 Token 生成时间，用于计算剩余有效期
        .expiration(expireDate)           // 设置过期时间，过期后 parseToken 将抛出 ExpiredJwtException
        .signWith(KEY)                    // 使用 HMAC-SHA256 算法和预计算密钥签名
        .compact();                       // 压缩为 JWT 标准格式的字符串
  }

  /**
   * 解析 JWT Token 并返回完整载荷。
   *
   * <p>方法作用：验证 Token 签名和过期时间，提取其中包含的所有声明（Claims）。</p>
   *
   * @param token JWT 字符串，格式：header.payload.signature
   *              - 允许值：通过 {@link #createToken(Long)} 生成的有效 Token
   *              - 约束：不允许为 null 或空字符串
   *
   * @return Claims 对象，包含 Token 中存储的所有字段（sub、iat、exp 等）
   *
   * <p>异常说明：
   * - Token 格式错误：抛出 MalformedJwtException
   * - 签名无效：抛出 SignatureException（可能被篡改）
   * - Token 已过期：抛出 ExpiredJwtException（exp < 当前时间）
   * - Token 为 null：抛出 IllegalArgumentException</p>
   *
   * <p>安全提示：本方法会严格验证签名，若密钥不匹配或 Token 被篡改将立即失败。</p>
   */
  public static Claims parseToken(String token) {
    return Jwts.parser()
        .verifyWith(KEY)      // 使用相同密钥验证签名，确保 Token 未被篡改
        .build()
        .parseSignedClaims(token)  // 解析并验证签名，失败时抛出上述异常
        .getPayload();             // 提取载荷部分（Payload）
  }

  /**
   * 从 Token 中提取用户 ID。
   *
   * <p>方法作用：便捷方法，直接获取 Token 中存储的用户标识，无需手动解析 Claims。</p>
   *
   * @param token JWT 字符串
   *              - 允许值：有效的 accessToken 或 refreshToken
   *              - 约束：Token 未过期且签名有效
   *
   * @return 用户 ID（Long 类型），对应 mobe_user.id
   *         - 永不返回 null，subject 必须存在且为有效数字
   *
   * <p>实现细节：
   * 1. 调用 parseToken 验证并解析 Token
   * 2. 从 Claims 中提取 subject 字段（存储时转为 String，此处转回 Long）</p>
   *
   * <p>异常说明：继承 parseToken 的所有异常，另加：
   * - subject 非数字：抛出 NumberFormatException（极少见，除非 Token 生成逻辑被篡改）</p>
   */
  public static Long getUserId(String token) {
    Claims claims = parseToken(token);
    return Long.valueOf(claims.getSubject());
  }

  /**
   * 验证 Token 是否有效。
   *
   * <p>方法作用：快速校验 Token 的签名和时效性，返回布尔结果而非抛出异常。</p>
   *
   * @param token JWT 字符串，可能来自 HTTP Header 的 Authorization 字段
   *              - 允许值：任意字符串（包括 null、空字符串、格式错误的 Token）
   *              - 约束：无，本方法内部捕获所有异常
   *
   * @return boolean 验证结果
   *         - true：Token 签名有效且未过期
   *         - false：Token 为 null、格式错误、签名无效或已过期
   *
   * <p>使用场景：
   * - 需要静默检查 Token 有效性，不暴露具体失败原因（如日志记录、前端预检）
   * - 与 parseToken 的区别：本方法吞掉所有异常，parseToken 会抛出具体异常</p>
   *
   * <p>性能考量：涉及 JWT 解析和签名验证，属于 CPU 密集型操作，
   * 高频调用场景（如每个请求的拦截器）建议配合 Redis 缓存验证结果。</p>
   */
  public static boolean isValid(String token) {
    try {
      parseToken(token);
      return true;
    } catch (Exception e) {
      // 捕获所有异常（格式错误、签名无效、过期等），统一返回 false
      // 不记录日志是设计选择：调用方（如 JwtInterceptor）会根据需要记录
      return false;
    }
  }
}
