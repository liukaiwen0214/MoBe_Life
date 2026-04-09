package com.mobe.mobe_life_backend.common.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.DigestUtil;

public class VerificationCodeUtils {

  private static final String CODE_SALT = "mobe_email_code_salt";

  private VerificationCodeUtils() {
  }

  public static String generateCode() {
    return RandomUtil.randomNumbers(6);
  }

  public static String hashCode(String target, String bizType, String code) {
    return DigestUtil.sha256Hex(target + ":" + bizType + ":" + code + ":" + CODE_SALT);
  }
}