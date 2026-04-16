package com.mobe.mobe_life_backend.task.controller;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.task.dto.TaskListQueryDTO;
import com.mobe.mobe_life_backend.task.service.MobeTaskService;
import com.mobe.mobe_life_backend.task.vo.TaskDetailVO;
import com.mobe.mobe_life_backend.task.vo.TaskListItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "待办模块", description = "待办相关接口")
public class TaskController {

  @Resource
  private MobeTaskService mobeTaskService;

  @GetMapping
  @Operation(summary = "获取待办列表", description = "分页获取当前登录用户的待办列表")
  public Result<PageResult<TaskListItemVO>> getTaskList(@Valid TaskListQueryDTO queryDTO) {
    return Result.success(mobeTaskService.getTaskList(queryDTO));
  }

  @GetMapping("/{id}")
  @Operation(summary = "获取待办详情", description = "获取当前登录用户的待办详情")
  public Result<TaskDetailVO> getTaskDetail(
      @Parameter(description = "待办ID") @PathVariable Long id) {
    return Result.success(mobeTaskService.getTaskDetail(id));
  }
}