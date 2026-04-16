package com.mobe.mobe_life_backend.task.service;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.task.dto.TaskListQueryDTO;
import com.mobe.mobe_life_backend.task.vo.TaskDetailVO;
import com.mobe.mobe_life_backend.task.vo.TaskListItemVO;

/**
 * 任务服务接口
 */
public interface MobeTaskService {
  PageResult<TaskListItemVO> getTaskList(TaskListQueryDTO queryDTO);

  TaskDetailVO getTaskDetail(Long id);
}
