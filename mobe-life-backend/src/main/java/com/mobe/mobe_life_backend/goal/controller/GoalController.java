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

  @GetMapping
  @Operation(summary = "获取目标列表", description = "分页获取当前登录用户的目标列表")
  public Result<PageResult<GoalListItemVO>> getGoalList(@Valid GoalListQueryDTO queryDTO) {
    return Result.success(mobeGoalService.getGoalList(queryDTO));
  }

  @GetMapping("/{id}")
  @Operation(summary = "获取目标详情", description = "获取当前登录用户的目标详情")
  public Result<GoalDetailVO> getGoalDetail(
      @Parameter(description = "目标ID") @PathVariable Long id) {
    return Result.success(mobeGoalService.getGoalDetail(id));
  }

  @Operation(summary = "赋予目标完成态")
  @PostMapping("/{id}/complete")
  public Result<Boolean> completeGoal(
      @Parameter(description = "目标ID", required = true) @PathVariable Long id,
      HttpServletRequest request) {
    mobeGoalService.completeGoal(id, request);
    return Result.success(true);
  }

  @Operation(summary = "恢复目标")
  @PostMapping("/{id}/reopen")
  public Result<Boolean> reopenGoal(
      @Parameter(description = "目标ID", required = true) @PathVariable Long id) {
    mobeGoalService.reopenGoal(id);
    return Result.success(true);
  }

  @Operation(summary = "恢复目标并恢复其下全部节点")
  @PostMapping("/{id}/reopen-all")
  public Result<Boolean> reopenGoalWithNodes(
      @Parameter(description = "目标ID", required = true) @PathVariable Long id) {
    mobeGoalService.reopenGoalWithNodes(id);
    return Result.success(true);
  }
}