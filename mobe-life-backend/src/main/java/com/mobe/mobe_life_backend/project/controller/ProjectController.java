/**
 * 核心职责：对外暴露项目中心相关接口，负责接收请求并调用对应业务能力。
 * 所属业务模块：项目中心 / 控制层。
 * 重要依赖关系或外部约束：依赖 Spring MVC 与服务层；通常不承载复杂业务逻辑。
 */
package com.mobe.mobe_life_backend.project.controller;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.project.dto.ProjectListQueryDTO;
import com.mobe.mobe_life_backend.project.service.MobeProjectService;
import com.mobe.mobe_life_backend.project.vo.ProjectListItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mobe.mobe_life_backend.project.vo.ProjectDetailVO;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 项目模块接口
 */
@RestController
@RequestMapping("/api/projects")
@Tag(name = "项目模块", description = "项目相关接口")
public class ProjectController {

  @Resource
  private MobeProjectService mobeProjectService;

  /**
   * 获取ProjectList。
   *
   * @return 返回对应结果。
   */
  @GetMapping
  @Operation(summary = "获取项目列表", description = "分页获取当前登录用户的项目列表")
  public Result<PageResult<ProjectListItemVO>> getProjectList(@Valid ProjectListQueryDTO queryDTO) {
    return Result.success(mobeProjectService.getProjectList(queryDTO));
  }

  /**
   * 获取项目详情
   * <p>
   * 根据项目ID获取当前登录用户的项目详细信息
   * </p>
   * 
   * @param id 项目ID
   * @return 项目详情信息，包含项目基本信息、状态、创建时间等
   * @see MobeProjectService#getProjectDetail(Long)
   */
  @GetMapping("/{id}")
  @Operation(summary = "获取项目详情", description = "获取当前登录用户的项目详情")
  public Result<ProjectDetailVO> getProjectDetail(
      @Parameter(description = "项目ID") @PathVariable Long id) {
    return Result.success(mobeProjectService.getProjectDetail(id));
  }

  /**
   * 将项目标记为完成。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "赋予项目完成态")
  @PostMapping("/{id}/complete")
  public Result<Boolean> completeProject(
      @Parameter(description = "项目ID", required = true) @PathVariable Long id,
      HttpServletRequest request) {
    mobeProjectService.completeProject(id, request);
    return Result.success(true);
  }

  /**
   * 恢复项目到未完成状态。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "恢复项目")
  @PostMapping("/{id}/reopen")
  public Result<Boolean> reopenProject(
      @Parameter(description = "项目ID", required = true) @PathVariable Long id) {
    mobeProjectService.reopenProject(id);
    return Result.success(true);
  }

  /**
   * 恢复项目并恢复其下全部节点。
   *
   * @return 成功时返回 `true`。
   */
  @Operation(summary = "恢复项目并恢复其下全部节点")
  @PostMapping("/{id}/reopen-all")
  public Result<Boolean> reopenProjectWithNodes(
      @Parameter(description = "项目ID", required = true) @PathVariable Long id) {
    mobeProjectService.reopenProjectWithNodes(id);
    return Result.success(true);
  }
}
