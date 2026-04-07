package com.mobe.mobe_life_backend;

import com.mobe.mobe_life_backend.common.utils.JwtUtils;
import com.mobe.mobe_life_backend.common.utils.PasswordUtils;
import org.junit.jupiter.api.Test;

public class CommonUtilsTest {

  @Test
  void testPasswordUtils() {
    String rawPassword = "123456";
    String encodedPassword = PasswordUtils.encode(rawPassword);

    System.out.println("加密后密码: " + encodedPassword);
    System.out.println("密码是否匹配: " + PasswordUtils.matches(rawPassword, encodedPassword));
  }

  @Test
  void testJwtUtils() {
    String token = JwtUtils.createToken(1L);

    System.out.println("token: " + token);
    System.out.println("userId: " + JwtUtils.getUserId(token));
    System.out.println("isValid: " + JwtUtils.isValid(token));
  }
}