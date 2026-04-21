package com.mobe.mobe_life_backend.project.service.impl;

import com.mobe.mobe_life_backend.project.service.MobeProjectService;
import com.mobe.mobe_life_backend.common.context.UserContext;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.common.exception.AuthErrorCode;
import com.mobe.mobe_life_backend.common.exception.ProjectErrorCode;
import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.project.dto.ProjectListQueryDTO;
import com.mobe.mobe_life_backend.project.entity.MobeProject;
import com.mobe.mobe_life_backend.project.vo.ProjectListItemVO;
import com.mobe.mobe_life_backend.project.mapper.MobeProjectMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mobe.mobe_life_backend.node.entity.MobeNode;
import com.mobe.mobe_life_backend.node.mapper.MobeNodeMapper;
import com.mobe.mobe_life_backend.project.vo.ProjectDetailVO;
import com.mobe.mobe_life_backend.project.vo.ProjectLogItemVO;
import com.mobe.mobe_life_backend.project.vo.ProjectNodeItemVO;
import com.mobe.mobe_life_backend.project.vo.ProjectStatusItemVO;
import com.mobe.mobe_life_backend.project.vo.ProjectTaskItemVO;
import com.mobe.mobe_life_backend.task.entity.MobeTaskItem;
import com.mobe.mobe_life_backend.task.entity.MobeTaskOperationLog;
import com.mobe.mobe_life_backend.task.entity.MobeTaskStatus;
import com.mobe.mobe_life_backend.task.mapper.MobeTaskItemMapper;
import com.mobe.mobe_life_backend.task.mapper.MobeTaskOperationLogMapper;
import com.mobe.mobe_life_backend.task.mapper.MobeTaskStatusMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MobeProjectServiceImpl implements MobeProjectService {
  private final MobeProjectMapper mobeProjectMapper;
  private final MobeNodeMapper mobeNodeMapper;
  private final MobeTaskItemMapper mobeTaskItemMapper;
  private final MobeTaskStatusMapper mobeTaskStatusMapper;
  private final MobeTaskOperationLogMapper mobeTaskOperationLogMapper;

  public MobeProjectServiceImpl(MobeProjectMapper mobeProjectMapper,
      MobeNodeMapper mobeNodeMapper,
      MobeTaskItemMapper mobeTaskItemMapper,
      MobeTaskStatusMapper mobeTaskStatusMapper,
      MobeTaskOperationLogMapper mobeTaskOperationLogMapper) {
    this.mobeProjectMapper = mobeProjectMapper;
    this.mobeNodeMapper = mobeNodeMapper;
    this.mobeTaskItemMapper = mobeTaskItemMapper;
    this.mobeTaskStatusMapper = mobeTaskStatusMapper;
    this.mobeTaskOperationLogMapper = mobeTaskOperationLogMapper;
  }

  @Override
  public PageResult<ProjectListItemVO> getProjectList(ProjectListQueryDTO queryDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    int pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
    int pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : queryDTO.getPageSize();
    long offset = (long) (pageNum - 1) * pageSize;
    Long total = mobeProjectMapper.countProjectList(
        userId,
        queryDTO.getKeyword(),
        queryDTO.getIncludeCompleted());

    if (total == null || total == 0) {
      return PageResult.empty(pageNum, pageSize);
    }

    List<ProjectListItemVO> list = mobeProjectMapper.selectProjectList(
        userId,
        queryDTO.getKeyword(),
        queryDTO.getIncludeCompleted(),
        offset,
        pageSize);

    return PageResult.of(total, pageNum, pageSize, list);
  }

  @Override
  public ProjectDetailVO getProjectDetail(Long id) {
    System.out.println(">>> project detail service hit");
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    ProjectDetailVO detailVO = mobeProjectMapper.selectProjectBaseDetail(id, userId);
    if (detailVO == null) {
      throw new BusinessException(ProjectErrorCode.PROJECT_NOT_FOUND);
    }

    // 1. 查询项目下节点
    List<MobeNode> nodeList = mobeNodeMapper.selectList(
        new LambdaQueryWrapper<MobeNode>()
            .eq(MobeNode::getUserId, userId)
            .eq(MobeNode::getOwnerType, "PROJECT")
            .eq(MobeNode::getOwnerId, id)
            .eq(MobeNode::getIsDeleted, 0)
            .orderByAsc(MobeNode::getSortNo)
            .orderByAsc(MobeNode::getId));

    List<Long> nodeIds = nodeList.stream().map(MobeNode::getId).toList();

    Map<Long, String> nodeNameMap = nodeList.stream()
        .collect(Collectors.toMap(MobeNode::getId, MobeNode::getTitle));

    // 2. 查询项目下待办（直接挂项目 + 挂节点）
    LambdaQueryWrapper<MobeTaskItem> taskWrapper = new LambdaQueryWrapper<MobeTaskItem>()
        .eq(MobeTaskItem::getUserId, userId)
        .eq(MobeTaskItem::getIsDeleted, 0)
        .and(w -> {
          w.eq(MobeTaskItem::getDirectOwnerType, "PROJECT")
              .eq(MobeTaskItem::getDirectOwnerId, id);
          if (!nodeIds.isEmpty()) {
            w.or()
                .eq(MobeTaskItem::getDirectOwnerType, "NODE")
                .in(MobeTaskItem::getDirectOwnerId, nodeIds);
          }
        })
        .orderByDesc(MobeTaskItem::getUpdateTime)
        .orderByDesc(MobeTaskItem::getId);

    List<MobeTaskItem> taskList = mobeTaskItemMapper.selectList(taskWrapper);

    // 3. 查状态列表（按项目绑定模板）
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

    Map<Long, MobeTaskStatus> statusMap = statusList.stream()
        .collect(Collectors.toMap(MobeTaskStatus::getId, item -> item));

    // 4. 组装节点列表
    Map<Long, Long> nodeTaskCountMap = taskList.stream()
        .filter(task -> "NODE".equals(task.getDirectOwnerType()) && task.getDirectOwnerId() != null)
        .collect(Collectors.groupingBy(MobeTaskItem::getDirectOwnerId, Collectors.counting()));

    List<ProjectNodeItemVO> nodeVOList = nodeList.stream().map(node -> {
      ProjectNodeItemVO vo = new ProjectNodeItemVO();
      vo.setId(node.getId());
      vo.setTitle(node.getTitle());
      vo.setTaskCount(nodeTaskCountMap.getOrDefault(node.getId(), 0L).intValue());
      return vo;
    }).toList();

    // 5. 组装待办列表
    List<ProjectTaskItemVO> taskVOList = taskList.stream().map(task -> {
      ProjectTaskItemVO vo = new ProjectTaskItemVO();
      vo.setId(task.getId());
      vo.setTitle(task.getTitle());
      vo.setDeadlineTime(task.getDeadlineTime());

      vo.setDirectOwnerType(task.getDirectOwnerType());
      vo.setDirectOwnerId(task.getDirectOwnerId());

      if ("NODE".equals(task.getDirectOwnerType()) && task.getDirectOwnerId() != null) {
        vo.setNodeName(nodeNameMap.get(task.getDirectOwnerId()));
      }

      MobeTaskStatus status = statusMap.get(task.getCurrentStatusId());
      if (status != null) {
        vo.setStatusCode(status.getStatusCode());
        vo.setStatusText(status.getStatusName());
      }
      return vo;
    }).toList();
    log.info("taskVOList: {}", taskVOList);
    // 6. 组装状态列表
    List<ProjectStatusItemVO> statusVOList = statusList.stream().map(status -> {
      ProjectStatusItemVO vo = new ProjectStatusItemVO();
      vo.setId(status.getId());
      vo.setStatusName(status.getStatusName());
      vo.setStatusCode(status.getStatusCode());
      vo.setIsInitial(status.getIsInitial());
      vo.setIsTerminal(status.getIsTerminal());
      vo.setIsEnabled(status.getIsEnabled());
      return vo;
    }).toList();

    // 7. 查询日志（先只取项目相关待办操作日志）
    List<ProjectLogItemVO> logVOList = new ArrayList<>();
    if (!taskList.isEmpty()) {
      List<Long> taskIds = taskList.stream().map(MobeTaskItem::getId).toList();

      List<MobeTaskOperationLog> operationLogs = mobeTaskOperationLogMapper.selectList(
          new LambdaQueryWrapper<MobeTaskOperationLog>()
              .eq(MobeTaskOperationLog::getUserId, userId)
              .eq(MobeTaskOperationLog::getIsDeleted, 0)
              .in(MobeTaskOperationLog::getTaskItemId, taskIds)
              .orderByDesc(MobeTaskOperationLog::getCreateTime)
              .last("LIMIT 20"));

      logVOList = operationLogs.stream().map(log -> {
        ProjectLogItemVO vo = new ProjectLogItemVO();
        vo.setId(log.getId());
        vo.setLogType(log.getOperationType());
        vo.setLogText(log.getOperationDesc());
        vo.setCreateTime(log.getCreateTime());
        return vo;
      }).toList();
    }

    detailVO.setNodes(nodeVOList);
    detailVO.setTasks(taskVOList);
    detailVO.setStatusList(statusVOList);
    detailVO.setLogs(logVOList);

    return detailVO;
  }

  @Override
  public void completeProject(Long id, HttpServletRequest request) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeProject project = mobeProjectMapper.selectById(id);
    if (project == null || Integer.valueOf(1).equals(project.getIsDeleted())) {
      throw new BusinessException(ProjectErrorCode.PROJECT_NOT_FOUND);
    }

    if (!userId.equals(project.getUserId())) {
      throw new BusinessException(AuthErrorCode.NO_PERMISSION);
    }

    if (Integer.valueOf(1).equals(project.getIsCompleted())) {
      throw new BusinessException("项目已是完成状态");
    }
    List<MobeNode> nodeList = mobeNodeMapper.selectList(
        new LambdaQueryWrapper<MobeNode>()
            .eq(MobeNode::getUserId, userId)
            .eq(MobeNode::getOwnerType, "PROJECT")
            .eq(MobeNode::getOwnerId, id)
            .eq(MobeNode::getIsDeleted, 0));

    List<Long> nodeIds = nodeList.stream().map(MobeNode::getId).toList();

    LambdaQueryWrapper<MobeTaskItem> taskWrapper = new LambdaQueryWrapper<MobeTaskItem>()
        .eq(MobeTaskItem::getUserId, userId)
        .eq(MobeTaskItem::getIsDeleted, 0)
        .and(w -> {
          w.eq(MobeTaskItem::getDirectOwnerType, "PROJECT")
              .eq(MobeTaskItem::getDirectOwnerId, id);
          if (!nodeIds.isEmpty()) {
            w.or()
                .eq(MobeTaskItem::getDirectOwnerType, "NODE")
                .in(MobeTaskItem::getDirectOwnerId, nodeIds);
          }
        });

    List<MobeTaskItem> taskList = mobeTaskItemMapper.selectList(taskWrapper);

    if (!taskList.isEmpty()) {
      List<Long> statusIds = taskList.stream()
          .map(MobeTaskItem::getCurrentStatusId)
          .filter(java.util.Objects::nonNull)
          .distinct()
          .toList();

      if (!statusIds.isEmpty()) {
        List<MobeTaskStatus> statusList = mobeTaskStatusMapper.selectList(
            new LambdaQueryWrapper<MobeTaskStatus>()
                .in(MobeTaskStatus::getId, statusIds)
                .eq(MobeTaskStatus::getIsDeleted, 0));

        Map<Long, MobeTaskStatus> statusMap = statusList.stream()
            .collect(Collectors.toMap(MobeTaskStatus::getId, item -> item));

        boolean hasUnfinishedTask = taskList.stream().anyMatch(task -> {
          MobeTaskStatus status = statusMap.get(task.getCurrentStatusId());
          return status == null || !Integer.valueOf(1).equals(status.getIsTerminal());
        });

        if (hasUnfinishedTask) {
          throw new BusinessException("该项目下仍有未完成待办，不能赋予完成态");
        }
      }
    }
    project.setIsCompleted(1);
    project.setCompletedTime(LocalDateTime.now());
    project.setUpdatedBy(userId);
    if (!nodeList.isEmpty()) {
      for (MobeNode node : nodeList) {
        if (!Integer.valueOf(1).equals(node.getIsCompleted())) {
          node.setIsCompleted(1);
          node.setCompletedTime(LocalDateTime.now());
          node.setUpdatedBy(userId);
          mobeNodeMapper.updateById(node);
        }
      }
    }
    mobeProjectMapper.updateById(project);
  }

  private MobeProject checkProjectForReopen(Long id, Long userId) {
    MobeProject project = mobeProjectMapper.selectById(id);
    if (project == null || Integer.valueOf(1).equals(project.getIsDeleted())) {
      throw new BusinessException(ProjectErrorCode.PROJECT_NOT_FOUND);
    }

    if (!userId.equals(project.getUserId())) {
      throw new BusinessException(AuthErrorCode.NO_PERMISSION);
    }

    if (!Integer.valueOf(1).equals(project.getIsCompleted())) {
      throw new BusinessException("该项目当前不是完成状态");
    }

    return project;
  }

  private void doReopenProject(MobeProject project, Long userId) {
    project.setIsCompleted(0);
    project.setCompletedTime(null);
    project.setUpdatedBy(userId);
    mobeProjectMapper.updateById(project);
  }

  private void doReopenProjectNodes(Long projectId, Long userId) {
    List<MobeNode> nodeList = mobeNodeMapper.selectList(
        new LambdaQueryWrapper<MobeNode>()
            .eq(MobeNode::getUserId, userId)
            .eq(MobeNode::getOwnerType, "PROJECT")
            .eq(MobeNode::getOwnerId, projectId)
            .eq(MobeNode::getIsDeleted, 0));

    if (nodeList.isEmpty()) {
      return;
    }

    for (MobeNode node : nodeList) {
      if (Integer.valueOf(1).equals(node.getIsCompleted())) {
        node.setIsCompleted(0);
        node.setCompletedTime(null);
        node.setUpdatedBy(userId);
        mobeNodeMapper.updateById(node);
      }
    }
  }

  @Override
  public void reopenProject(Long id) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeProject project = checkProjectForReopen(id, userId);
    doReopenProject(project, userId);
  }

  @Override
  public void reopenProjectWithNodes(Long id) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeProject project = checkProjectForReopen(id, userId);
    doReopenProject(project, userId);
    doReopenProjectNodes(id, userId);
  }

}
