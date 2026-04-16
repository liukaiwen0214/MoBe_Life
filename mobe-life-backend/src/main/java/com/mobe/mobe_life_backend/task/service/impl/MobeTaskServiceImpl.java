package com.mobe.mobe_life_backend.task.service.impl;

import org.springframework.stereotype.Service;
import com.mobe.mobe_life_backend.task.service.MobeTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mobe.mobe_life_backend.common.context.UserContext;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.common.exception.AuthErrorCode;
import com.mobe.mobe_life_backend.common.exception.TaskErrorCode;
import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.task.dto.TaskListQueryDTO;
import com.mobe.mobe_life_backend.task.entity.MobeTaskOperationLog;
import com.mobe.mobe_life_backend.task.entity.MobeTaskStatus;
import com.mobe.mobe_life_backend.task.mapper.MobeTaskItemMapper;
import com.mobe.mobe_life_backend.task.mapper.MobeTaskOperationLogMapper;
import com.mobe.mobe_life_backend.task.mapper.MobeTaskStatusMapper;
import com.mobe.mobe_life_backend.task.vo.TaskDetailVO;
import com.mobe.mobe_life_backend.task.vo.TaskListItemVO;
import com.mobe.mobe_life_backend.task.vo.TaskLogItemVO;
import com.mobe.mobe_life_backend.task.vo.TaskStatusItemVO;

import java.util.Collections;
import java.util.List;

/**
 * 任务服务实现类
 */
@Service
public class MobeTaskServiceImpl implements MobeTaskService {
  private final MobeTaskItemMapper mobeTaskItemMapper;
  private final MobeTaskStatusMapper mobeTaskStatusMapper;
  private final MobeTaskOperationLogMapper mobeTaskOperationLogMapper;

  public MobeTaskServiceImpl(MobeTaskItemMapper mobeTaskItemMapper,
      MobeTaskStatusMapper mobeTaskStatusMapper,
      MobeTaskOperationLogMapper mobeTaskOperationLogMapper) {
    this.mobeTaskItemMapper = mobeTaskItemMapper;
    this.mobeTaskStatusMapper = mobeTaskStatusMapper;
    this.mobeTaskOperationLogMapper = mobeTaskOperationLogMapper;
  }

  @Override
  public PageResult<TaskListItemVO> getTaskList(TaskListQueryDTO queryDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    int pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
    int pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : queryDTO.getPageSize();
    long offset = (long) (pageNum - 1) * pageSize;

    Long total = mobeTaskItemMapper.countTaskList(
        userId,
        queryDTO.getKeyword(),
        queryDTO.getStatusCode(),
        queryDTO.getDirectOwnerType());

    if (total == null || total == 0) {
      return PageResult.empty(pageNum, pageSize);
    }

    List<TaskListItemVO> list = mobeTaskItemMapper.selectTaskList(
        userId,
        queryDTO.getKeyword(),
        queryDTO.getStatusCode(),
        queryDTO.getDirectOwnerType(),
        offset,
        pageSize);

    return PageResult.of(total, pageNum, pageSize, list);
  }

  @Override
  public TaskDetailVO getTaskDetail(Long id) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    TaskDetailVO detailVO = mobeTaskItemMapper.selectTaskBaseDetail(id, userId);
    if (detailVO == null) {
      throw new BusinessException(TaskErrorCode.TASK_NOT_FOUND);
    }

    // 1. 查状态列表（按待办绑定模板）
    List<MobeTaskStatus> statusList = Collections.emptyList();
    if (detailVO.getStatusTemplateId() != null) {
      statusList = mobeTaskStatusMapper.selectList(
          new LambdaQueryWrapper<MobeTaskStatus>()
              .eq(MobeTaskStatus::getUserId, userId)
              .eq(MobeTaskStatus::getTemplateId, detailVO.getStatusTemplateId())
              .eq(MobeTaskStatus::getIsDeleted, 0)
              .orderByAsc(MobeTaskStatus::getSortNo)
              .orderByAsc(MobeTaskStatus::getId));
    }

    List<TaskStatusItemVO> statusVOList = statusList.stream().map(status -> {
      TaskStatusItemVO vo = new TaskStatusItemVO();
      vo.setId(status.getId());
      vo.setStatusName(status.getStatusName());
      vo.setStatusCode(status.getStatusCode());
      vo.setIsInitial(status.getIsInitial());
      vo.setIsTerminal(status.getIsTerminal());
      vo.setIsEnabled(status.getIsEnabled());
      return vo;
    }).toList();

    // 2. 查待办日志
    List<MobeTaskOperationLog> operationLogs = mobeTaskOperationLogMapper.selectList(
        new LambdaQueryWrapper<MobeTaskOperationLog>()
            .eq(MobeTaskOperationLog::getUserId, userId)
            .eq(MobeTaskOperationLog::getTaskItemId, id)
            .eq(MobeTaskOperationLog::getIsDeleted, 0)
            .orderByDesc(MobeTaskOperationLog::getCreateTime)
            .last("LIMIT 20"));

    List<TaskLogItemVO> logVOList = operationLogs.stream().map(log -> {
      TaskLogItemVO vo = new TaskLogItemVO();
      vo.setId(log.getId());
      vo.setLogType(log.getOperationType());
      vo.setLogText(log.getOperationDesc());
      vo.setCreateTime(log.getCreateTime());
      return vo;
    }).toList();

    detailVO.setStatusList(statusVOList);
    detailVO.setLogs(logVOList);

    return detailVO;
  }
}
