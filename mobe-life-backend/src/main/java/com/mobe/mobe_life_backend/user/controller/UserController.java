/**
 * 核心职责：对外暴露用户中心相关接口，负责接收请求并调用对应业务能力。
 * 所属业务模块：用户中心 / 控制层。
 * 重要依赖关系或外部约束：依赖 Spring MVC 与服务层；通常不承载复杂业务逻辑。
 */
package com.mobe.mobe_life_backend.user.controller;

import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.user.dto.UnbindEmailDTO;
import com.mobe.mobe_life_backend.user.dto.UpdateUserProfileDTO;
import com.mobe.mobe_life_backend.user.service.MobeUserService;
import com.mobe.mobe_life_backend.user.vo.CurrentUserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户模块接口", description = "提供当前登录用户资料查询、资料维护与邮箱解绑接口")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  private final MobeUserService mobeUserService;

  /**
   * 获取CurrentUser。
   *
   * @return 返回对应结果。
   */
  @Operation(summary = "获取当前用户信息", description = "查询当前登录用户的个人资料、绑定状态与账号基础信息")
  @GetMapping("/current")
  public Result<CurrentUserVO> getCurrentUser() {
    return Result.success(mobeUserService.getCurrentUser());
  }

  /**
   * 更新当前登录用户资料。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "更新当前用户资料", description = "按提交字段更新当前登录用户的昵称、头像、性别和生日信息")
  @PostMapping("/profile/update")
  public Result<Boolean> updateCurrentUserProfile(
      @RequestBody UpdateUserProfileDTO updateUserProfileDTO) {
    mobeUserService.updateCurrentUserProfile(updateUserProfileDTO);
    return Result.success(true);
  }

  /**
   * 解绑当前账号邮箱。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "解绑邮箱", description = "校验解绑验证码后解除当前账号与邮箱的绑定关系")
  @PostMapping("/unbind-email")
  public Result<Boolean> unbindEmail(
      @RequestBody @Valid UnbindEmailDTO unbindEmailDTO) {
    mobeUserService.unbindEmail(unbindEmailDTO);
    return Result.success(true);
  }
}
