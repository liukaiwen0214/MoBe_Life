/**
 * 核心职责：提供 JWT 的生成、解析和校验能力。
 * 所属业务模块：公共基础设施 / 认证工具。
 * 重要依赖关系或外部约束：依赖 JJWT 和对称密钥签名；当前密钥硬编码仅适合开发阶段，生产环境应外置。
 */
package com.mobe.mobe_life_backend.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类。
 *
 * <p>设计初衷是把 token 协议细节封装起来，让业务代码只围绕“生成 token”“取出用户 ID”“判断是否有效”这些稳定语义编程。</p>
 *
 * <p>线程安全性：纯静态无状态工具类；预构建的 `SecretKey` 在类加载后只读，线程安全。</p>
 */
public class JwtUtils {

  /** JWT 对称签名密钥。 */
  private static final String SECRET_KEY = "mobe-life-secret-key-2026-very-safe";

  /** token 有效期，当前固定为 7 天。 */
  private static final long EXPIRE_TIME = 7L * 24 * 60 * 60 * 1000;

  /** 预先构建的签名密钥，避免重复创建。 */
  private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

  private JwtUtils() {
  }

  /**
   * 生成 token。
   *
   * @param userId 用户主键，不允许为 null。
   * @return JWT 字符串，不返回 null。
   */
  public static String createToken(Long userId) {
    Date now = new Date();
    Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

    return Jwts.builder()
        .subject(String.valueOf(userId))
        .issuedAt(now)
        .expiration(expireDate)
        .signWith(KEY)
        .compact();
  }

  /**
   * 解析 token 载荷。
   *
   * @param token JWT 字符串，不允许为 null 或空白。
   * @return token 载荷，不返回 null。
   * @throws RuntimeException 当 token 格式错误、签名无效或已过期时抛出。
   */
  public static Claims parseToken(String token) {
    return Jwts.parser()
        .verifyWith(KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * 从 token 中提取用户 ID。
   *
   * @param token JWT 字符串，不允许为 null。
   * @return 用户 ID，不返回 null。
   * @throws RuntimeException 当 token 无法解析或 subject 不是有效数字时抛出。
   */
  public static Long getUserId(String token) {
    Claims claims = parseToken(token);
    return Long.valueOf(claims.getSubject());
  }

  /**
   * 判断 token 是否有效。
   *
   * @param token 任意待校验字符串，允许为 null。
   * @return 合法且未过期返回 `true`，否则返回 `false`。
   * @implNote 这里故意吞掉异常，是为了让调用方可以只基于布尔值处理鉴权分支。
   */
  public static boolean isValid(String token) {
    try {
      parseToken(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
