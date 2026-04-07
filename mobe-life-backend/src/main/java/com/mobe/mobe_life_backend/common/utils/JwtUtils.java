package com.mobe.mobe_life_backend.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtUtils {

  /**
   * 至少 32 位，保证 HMAC SHA 安全长度
   */
  private static final String SECRET_KEY = "mobe-life-secret-key-2026-very-safe";
  private static final long EXPIRE_TIME = 7L * 24 * 60 * 60 * 1000;

  private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

  private JwtUtils() {
  }

  /**
   * 生成 token
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
   * 解析 token
   */
  public static Claims parseToken(String token) {
    return Jwts.parser()
        .verifyWith(KEY)
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  /**
   * 从 token 获取用户 id
   */
  public static Long getUserId(String token) {
    Claims claims = parseToken(token);
    return Long.valueOf(claims.getSubject());
  }

  /**
   * 判断 token 是否有效
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