package com.mobe.mobe_life_backend.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtils {

  private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

  private PasswordUtils() {
  }

  /**
   * 加密密码
   */
  public static String encode(String rawPassword) {
    return PASSWORD_ENCODER.encode(rawPassword);
  }

  /**
   * 校验密码
   */
  public static boolean matches(String rawPassword, String encodedPassword) {
    return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
  }
}