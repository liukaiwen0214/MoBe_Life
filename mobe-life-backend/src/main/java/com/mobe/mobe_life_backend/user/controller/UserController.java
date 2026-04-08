package com.mobe.mobe_life_backend.user.controller;

import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.user.dto.UpdateUserProfileDTO;
import com.mobe.mobe_life_backend.user.service.MobeUserService;
import com.mobe.mobe_life_backend.user.vo.CurrentUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final MobeUserService mobeUserService;

  @GetMapping("/current")
  public Result<CurrentUserVO> getCurrentUser() {
    return Result.success(mobeUserService.getCurrentUser());
  }

  @PostMapping("/profile/update")
  public Result<Boolean> updateCurrentUserProfile(@RequestBody UpdateUserProfileDTO updateUserProfileDTO) {
    mobeUserService.updateCurrentUserProfile(updateUserProfileDTO);
    return Result.success(true);
  }
}