package com.mobe.mobe_life_backend.auth.controller;

import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.exception.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

  @GetMapping("/test")
  public Result<String> test() {
    return Result.success("ok");
  }

  @GetMapping("/test/error")
  public Result<String> testError() {
    throw new BusinessException("测试业务异常");
  }
}