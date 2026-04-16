package com.mobe.mobe_life_backend.project.service;

import com.mobe.mobe_life_backend.project.dto.ProjectListQueryDTO;
import com.mobe.mobe_life_backend.project.vo.ProjectListItemVO;
import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.project.vo.ProjectDetailVO;

public interface MobeProjectService {
  PageResult<ProjectListItemVO> getProjectList(ProjectListQueryDTO queryDTO);

  ProjectDetailVO getProjectDetail(Long id);
}
