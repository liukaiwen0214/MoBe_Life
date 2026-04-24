/**
 * 核心职责：定义待办中心的业务能力边界，为控制层提供稳定调用接口。
 * 所属业务模块：待办中心 / 服务接口。
 * 重要依赖关系或外部约束：实现通常位于 `impl` 包中，接口方法语义应尽量稳定。
 */
package com.mobe.mobe_life_backend.task.service;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.task.dto.TaskCreateDTO;
import com.mobe.mobe_life_backend.task.dto.TaskListQueryDTO;
import com.mobe.mobe_life_backend.task.dto.TaskNextStatusDTO;
import com.mobe.mobe_life_backend.task.dto.TaskReleaseDTO;
import com.mobe.mobe_life_backend.task.dto.TaskUpdateDTO;
import com.mobe.mobe_life_backend.task.vo.TaskDetailVO;
import com.mobe.mobe_life_backend.task.vo.TaskFlowVO;
import com.mobe.mobe_life_backend.task.vo.TaskListItemVO;

/**
 * 任务服务接口
 */
public interface MobeTaskService {
  PageResult<TaskListItemVO> getTaskList(TaskListQueryDTO queryDTO);

  TaskDetailVO getTaskDetail(Long id);

  Long createTask(TaskCreateDTO dto);

  void moveTaskToNextStatus(Long id, TaskNextStatusDTO dto);

  void updateTask(Long id, TaskUpdateDTO dto);

  void deleteTask(Long id);

  TaskFlowVO getTaskFlow(Long id);

  void releaseTaskToStatus(Long id, TaskReleaseDTO dto);
}
