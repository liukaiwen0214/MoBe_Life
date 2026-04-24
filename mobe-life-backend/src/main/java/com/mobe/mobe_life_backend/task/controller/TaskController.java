/**
 * 核心职责：对外暴露待办列表与详情接口，负责把 HTTP 请求转换为任务查询用例调用。
 * 所属业务模块：待办中心 / 控制层。
 * 重要依赖关系或外部约束：依赖 `JwtInterceptor` 预先完成登录态校验；返回结构遵循统一 `Result` 包装。
 */
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

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mobe.mobe_life_backend.task.dto.TaskCreateDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.mobe.mobe_life_backend.task.dto.TaskNextStatusDTO;
import com.mobe.mobe_life_backend.task.dto.TaskUpdateDTO;
import com.mobe.mobe_life_backend.task.dto.TaskReleaseDTO;
import com.mobe.mobe_life_backend.task.vo.TaskFlowVO;

/**
 * 待办控制器。
 *
 * <p>
 * 设计初衷是保持控制层足够薄，只承担参数接收、接口文档暴露和结果包装，
 * 真正的业务聚合逻辑全部下沉到 `MobeTaskService`。
 * </p>
 */
@RestController
@RequestMapping("/api/tasks")
@Tag(name = "待办模块", description = "待办相关接口")
public class TaskController {

  /** 待办应用服务。 */
  @Resource
  private MobeTaskService mobeTaskService;

  /**
   * 获取待办列表。
   *
   * @param queryDTO 查询条件，支持页码、关键词、状态编码和直属归属类型过滤。
   * @return 统一结果包装后的分页待办列表。
   */
  @GetMapping
  @Operation(summary = "获取待办列表", description = "分页获取当前登录用户的待办列表")
  public Result<PageResult<TaskListItemVO>> getTaskList(@Valid TaskListQueryDTO queryDTO) {
    return Result.success(mobeTaskService.getTaskList(queryDTO));
  }

  /**
   * 获取待办详情。
   *
   * @param id 待办 ID。
   * @return 统一结果包装后的待办详情。
   */
  @GetMapping("/{id}")
  @Operation(summary = "获取待办详情", description = "获取当前登录用户的待办详情")
  public Result<TaskDetailVO> getTaskDetail(
      @Parameter(description = "待办ID") @PathVariable Long id) {
    return Result.success(mobeTaskService.getTaskDetail(id));
  }

  /**
   * 创建Task。
   *
   * @return 返回创建结果。
   */
  @Operation(summary = "新增待办", description = "新增当前登录用户的待办")
  @PostMapping
  public Result<Long> createTask(@RequestBody TaskCreateDTO dto) {
    return Result.success(mobeTaskService.createTask(dto));
  }

  /**
   * 将待办推进到下一个状态。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "待办进入下一个状态", description = "让当前待办按状态模板顺序进入下一个状态")
  @PostMapping("/{id}/next-status")
  public Result<Boolean> moveTaskToNextStatus(
      @Parameter(description = "待办ID", required = true) @PathVariable Long id,
      @RequestBody(required = false) TaskNextStatusDTO dto) {
    mobeTaskService.moveTaskToNextStatus(id, dto);
    return Result.success(true);
  }

  /**
   * 更新待办信息。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "编辑待办", description = "编辑当前登录用户的待办")
  @PutMapping("/{id}")
  public Result<Boolean> updateTask(
      @Parameter(description = "待办ID", required = true) @PathVariable Long id,
      @RequestBody TaskUpdateDTO dto) {
    mobeTaskService.updateTask(id, dto);
    return Result.success(true);
  }

  /**
   * 删除待办。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "删除待办", description = "删除当前登录用户的待办")
  @DeleteMapping("/{id}")
  public Result<Boolean> deleteTask(
      @Parameter(description = "待办ID", required = true) @PathVariable Long id) {
    mobeTaskService.deleteTask(id);
    return Result.success(true);
  }

  @GetMapping("/{id}/flow")
  @Operation(summary = "获取待办流程视图", description = "获取当前待办的完整流程线路、下一状态与可放出状态")
  public Result<TaskFlowVO> getTaskFlow(
      @Parameter(description = "待办ID") @PathVariable Long id) {
    return Result.success(mobeTaskService.getTaskFlow(id));
  }

  @PostMapping("/{id}/release")
  @Operation(summary = "放出待办", description = "将终态待办放出到指定的非终态状态")
  public Result<Boolean> releaseTaskToStatus(
      @Parameter(description = "待办ID", required = true) @PathVariable Long id,
      @RequestBody TaskReleaseDTO dto) {
    mobeTaskService.releaseTaskToStatus(id, dto);
    return Result.success(true);
  }
}
