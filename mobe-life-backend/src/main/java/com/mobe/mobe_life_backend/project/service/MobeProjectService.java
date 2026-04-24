/**
 * 核心职责：定义项目中心的业务能力边界，为控制层提供稳定调用接口。
 * 所属业务模块：项目中心 / 服务接口。
 * 重要依赖关系或外部约束：实现通常位于 `impl` 包中，接口方法语义应尽量稳定。
 */
package com.mobe.mobe_life_backend.project.service;

import com.mobe.mobe_life_backend.project.dto.ProjectListQueryDTO;
import com.mobe.mobe_life_backend.project.vo.ProjectListItemVO;

import jakarta.servlet.http.HttpServletRequest;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.project.vo.ProjectDetailVO;

public interface MobeProjectService {
  PageResult<ProjectListItemVO> getProjectList(ProjectListQueryDTO queryDTO);

  ProjectDetailVO getProjectDetail(Long id);

  void completeProject(Long id, HttpServletRequest request);

  void reopenProject(Long id);

  void reopenProjectWithNodes(Long id);
}
