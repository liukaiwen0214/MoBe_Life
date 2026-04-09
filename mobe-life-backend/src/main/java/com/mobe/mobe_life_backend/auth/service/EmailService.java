package com.mobe.mobe_life_backend.auth.service;

public interface EmailService {

  void sendBindEmailCode(String toEmail, String code);
}