/**
 * 核心职责：对外暴露目标中心相关接口，负责接收请求并调用对应业务能力。
 * 所属业务模块：目标中心 / 控制层。
 * 重要依赖关系或外部约束：依赖 Spring MVC 与服务层；通常不承载复杂业务逻辑。
 */
package com.mobe.mobe_life_backend.goal.controller;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.goal.dto.GoalListQueryDTO;
import com.mobe.mobe_life_backend.goal.service.MobeGoalService;
import com.mobe.mobe_life_backend.goal.vo.GoalDetailVO;
import com.mobe.mobe_life_backend.goal.vo.GoalListItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goals")
@Tag(name = "目标模块", description = "目标相关接口")
public class GoalController {

  @Resource
  private MobeGoalService mobeGoalService;

  /**
   * 获取GoalList。
   *
   * @return 返回对应结果。
   */
  @GetMapping
  @Operation(summary = "获取目标列表", description = "分页获取当前登录用户的目标列表")
  public Result<PageResult<GoalListItemVO>> getGoalList(@Valid GoalListQueryDTO queryDTO) {
    return Result.success(mobeGoalService.getGoalList(queryDTO));
  }

  /**
   * 获取目标详情。
   *
   * @return 返回目标详情结果。
   */
  @GetMapping("/{id}")
  @Operation(summary = "获取目标详情", description = "获取当前登录用户的目标详情")
  public Result<GoalDetailVO> getGoalDetail(
      @Parameter(description = "目标ID") @PathVariable Long id) {
    return Result.success(mobeGoalService.getGoalDetail(id));
  }

  /**
   * 将目标标记为完成。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "赋予目标完成态")
  @PostMapping("/{id}/complete")
  public Result<Boolean> completeGoal(
      @Parameter(description = "目标ID", required = true) @PathVariable Long id,
      HttpServletRequest request) {
    mobeGoalService.completeGoal(id, request);
    return Result.success(true);
  }

  /**
   * 恢复目标到未完成状态。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "恢复目标")
  @PostMapping("/{id}/reopen")
  public Result<Boolean> reopenGoal(
      @Parameter(description = "目标ID", required = true) @PathVariable Long id) {
    mobeGoalService.reopenGoal(id);
    return Result.success(true);
  }

  /**
   * 恢复目标并恢复其下全部节点。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "恢复目标并恢复其下全部节点")
  @PostMapping("/{id}/reopen-all")
  public Result<Boolean> reopenGoalWithNodes(
      @Parameter(description = "目标ID", required = true) @PathVariable Long id) {
    mobeGoalService.reopenGoalWithNodes(id);
    return Result.success(true);
  }
}
