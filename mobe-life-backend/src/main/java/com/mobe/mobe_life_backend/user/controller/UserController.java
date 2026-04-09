/**
 * 核心职责：暴露当前登录用户资料查询与修改接口。
 * 所属业务模块：用户中心 / 表现层。
 * 重要依赖关系或外部约束：依赖 `MobeUserService` 和 JWT 鉴权上下文；接口默认面向“当前用户”而非任意用户。
 */
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

/**
 * 用户控制器。
 *
 * <p>设计初衷是把“当前登录用户自己能做的事情”收敛到独立入口，避免把用户中心接口做成管理员和普通用户共用的混合式 API。</p>
 *
 * <p>线程安全性：仅持有不可变依赖，线程安全。</p>
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

  /** 用户服务。 */
  private final MobeUserService mobeUserService;

  /**
   * 获取当前登录用户资料。
   *
   * @return 当前用户视图，不返回 null。
   * @throws RuntimeException 当当前请求未登录或用户已被删除时抛出。
   */
  @GetMapping("/current")
  public Result<CurrentUserVO> getCurrentUser() {
    return Result.success(mobeUserService.getCurrentUser());
  }

  /**
   * 更新当前登录用户资料。
   *
   * @param updateUserProfileDTO 更新参数，不允许为 null；允许只传需要修改的字段。
   * @return 固定返回 `true`。
   * @throws RuntimeException 当用户未登录、用户不存在或更新字段不满足业务约束时抛出。
   * @implNote 该接口会修改数据库中的用户资料。
   */
  @PostMapping("/profile/update")
  public Result<Boolean> updateCurrentUserProfile(@RequestBody UpdateUserProfileDTO updateUserProfileDTO) {
    mobeUserService.updateCurrentUserProfile(updateUserProfileDTO);
    return Result.success(true);
  }
}
