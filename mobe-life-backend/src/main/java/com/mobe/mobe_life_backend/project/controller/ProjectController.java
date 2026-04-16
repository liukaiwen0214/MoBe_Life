package com.mobe.mobe_life_backend.project.controller;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.project.dto.ProjectListQueryDTO;
import com.mobe.mobe_life_backend.project.service.MobeProjectService;
import com.mobe.mobe_life_backend.project.vo.ProjectListItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.mobe.mobe_life_backend.project.vo.ProjectDetailVO;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 项目模块接口
 */
@RestController
@RequestMapping("/api/projects")
@Tag(name = "项目模块", description = "项目相关接口")
public class ProjectController {

  @Resource
  private MobeProjectService mobeProjectService;

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
}