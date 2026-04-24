/**
 * 核心职责：聚合项目列表、项目详情以及项目完成/重开流程，把项目、节点、待办、状态和日志拼装成完整视图。
 * 所属业务模块：项目中心 / 业务服务实现。
 * 重要依赖关系或外部约束：项目完成态依赖下属节点和待办完成情况；需要确保所有查询都以当前用户为边界。
 */
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
  /** 项目主表访问入口。 */
  private final MobeProjectMapper mobeProjectMapper;
  /** 节点主表访问入口。 */
  private final MobeNodeMapper mobeNodeMapper;
  /** 待办主表访问入口。 */
  private final MobeTaskItemMapper mobeTaskItemMapper;
  /** 待办状态字典访问入口。 */
  private final MobeTaskStatusMapper mobeTaskStatusMapper;
  /** 待办操作日志访问入口。 */
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

  /**
   * 获取项目列表。
   *
   * @param queryDTO 分页和筛选条件。
   * @return 当前用户可见的项目分页结果。
   */
  @Override
  public PageResult<ProjectListItemVO> getProjectList(ProjectListQueryDTO queryDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    // 与待办、节点列表统一分页兜底规则，避免不同模块分页默认行为不一致。
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

  /**
   * 获取项目详情。
   *
   * @param id 项目 ID。
   * @return 包含节点、待办、状态和最近日志的项目详情视图。
   */
  @Override
  public ProjectDetailVO getProjectDetail(Long id) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    ProjectDetailVO detailVO = mobeProjectMapper.selectProjectBaseDetail(id, userId);
    if (detailVO == null) {
      throw new BusinessException(ProjectErrorCode.PROJECT_NOT_FOUND);
    }

    // 1. 查询项目下节点。项目详情页需要先知道节点集合，后续才能关联节点待办和节点计数。
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

    // 2. 查询项目下待办（直接挂项目 + 挂在项目节点上的待办都要纳入）。
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

    // 3. 查状态列表（按项目绑定模板），用于把任务上的状态 ID 还原成可展示文本。
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

    // 4. 组装节点列表，同时回填每个节点下挂载的待办数量。
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

    // 5. 组装待办列表，把原始任务实体转成详情页直接渲染的卡片结构。
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

    // 6. 组装状态列表。这里直接透出是否初始/终态，方便前端决定按钮和徽标展示。
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

    // 7. 查询日志。当前版本优先展示与项目下待办相关的最近操作，用于提供轻量动态感知。
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

  /**
   * 将项目标记为完成。
   *
   * @param id 项目 ID。
   * @param request 当前请求对象；当前版本未使用，但为后续操作审计预留。
   */
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
    // 项目完成前必须确认项目下所有待办都已进入终态，否则会出现父项目完成但子任务未完成的语义冲突。
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
                .eq(MobeTaskStatus::getUserId, userId)
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

  /**
   * 校验项目是否允许重开。
   *
   * @param id 项目 ID。
   * @param userId 当前登录用户 ID。
   * @return 已校验通过的项目实体。
   */
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

  /**
   * 将项目本身从完成态恢复为进行中。
   *
   * @param project 已校验的项目实体。
   * @param userId 当前登录用户 ID。
   */
  private void doReopenProject(MobeProject project, Long userId) {
    project.setIsCompleted(0);
    project.setCompletedTime(null);
    project.setUpdatedBy(userId);
    mobeProjectMapper.updateById(project);
  }

  /**
   * 重开项目下已完成的节点。
   *
   * @param projectId 项目 ID。
   * @param userId 当前登录用户 ID。
   */
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

  /**
   * 执行reopenProject。
   */
  @Override
  public void reopenProject(Long id) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeProject project = checkProjectForReopen(id, userId);
    doReopenProject(project, userId);
  }

  /**
   * 执行reopenProjectWithNodes。
   */
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
