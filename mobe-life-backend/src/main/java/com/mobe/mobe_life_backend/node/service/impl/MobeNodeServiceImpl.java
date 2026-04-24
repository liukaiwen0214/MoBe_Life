/**
 * 核心职责：提供节点列表、节点详情以及节点完成/重开相关用例，负责把节点与其下待办状态关联起来。
 * 所属业务模块：节点中心 / 业务服务实现。
 * 重要依赖关系或外部约束：节点状态依赖其下待办完成情况；节点只能操作当前登录用户自己的数据。
 */
package com.mobe.mobe_life_backend.node.service.impl;

import org.springframework.stereotype.Service;
import com.mobe.mobe_life_backend.node.service.MobeNodeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mobe.mobe_life_backend.common.context.UserContext;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.common.exception.GoalErrorCode;
import com.mobe.mobe_life_backend.common.exception.AuthErrorCode;
import com.mobe.mobe_life_backend.common.exception.NodeErrorCode;
import com.mobe.mobe_life_backend.common.exception.ProjectErrorCode;
import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.node.dto.NodeCreateDTO;
import com.mobe.mobe_life_backend.node.dto.NodeListQueryDTO;
import com.mobe.mobe_life_backend.node.entity.MobeNode;
import com.mobe.mobe_life_backend.node.mapper.MobeNodeMapper;
import com.mobe.mobe_life_backend.node.vo.NodeDetailVO;
import com.mobe.mobe_life_backend.node.vo.NodeListItemVO;
import com.mobe.mobe_life_backend.node.vo.NodeTaskItemVO;
import com.mobe.mobe_life_backend.project.entity.MobeProject;
import com.mobe.mobe_life_backend.task.entity.MobeTaskItem;
import com.mobe.mobe_life_backend.task.entity.MobeTaskStatus;
import com.mobe.mobe_life_backend.task.mapper.MobeTaskItemMapper;
import com.mobe.mobe_life_backend.task.mapper.MobeTaskStatusMapper;
import org.springframework.util.StringUtils;
import com.mobe.mobe_life_backend.project.mapper.MobeProjectMapper;
import com.mobe.mobe_life_backend.goal.entity.MobeGoal;
import com.mobe.mobe_life_backend.goal.mapper.MobeGoalMapper;
import com.mobe.mobe_life_backend.node.dto.NodeUpdateDTO;
import com.mobe.mobe_life_backend.node.dto.NodeDeleteDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 节点服务实现。
 *
 * <p>
 * 设计初衷是把“节点本身”和“节点下待办聚合视图”收敛到一个服务，
 * 避免控制层或前端自己拼接状态文本和完成约束。
 * </p>
 */
@Slf4j
@Service
public class MobeNodeServiceImpl implements MobeNodeService {
  /** 节点主表访问入口。 */
  private final MobeNodeMapper mobeNodeMapper;
  /** 待办主表访问入口。 */
  private final MobeTaskItemMapper mobeTaskItemMapper;
  /** 待办状态字典访问入口。 */
  private final MobeTaskStatusMapper mobeTaskStatusMapper;

  private final MobeProjectMapper mobeProjectMapper;
  private final MobeGoalMapper mobeGoalMapper;

  public MobeNodeServiceImpl(MobeNodeMapper mobeNodeMapper,
      MobeTaskItemMapper mobeTaskItemMapper,
      MobeTaskStatusMapper mobeTaskStatusMapper,
      MobeProjectMapper mobeProjectMapper,
      MobeGoalMapper mobeGoalMapper) {
    this.mobeNodeMapper = mobeNodeMapper;
    this.mobeTaskItemMapper = mobeTaskItemMapper;
    this.mobeTaskStatusMapper = mobeTaskStatusMapper;
    this.mobeProjectMapper = mobeProjectMapper;
    this.mobeGoalMapper = mobeGoalMapper;
  }

  /**
   * 获取节点列表。
   *
   * @param queryDTO 分页与筛选条件。
   * @return 当前用户可见的节点分页结果。
   */
  @Override
  public PageResult<NodeListItemVO> getNodeList(NodeListQueryDTO queryDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    // 与其他列表接口保持一致的默认分页策略，降低前端遗漏参数时的偶发行为差异。
    int pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
    int pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : queryDTO.getPageSize();
    long offset = (long) (pageNum - 1) * pageSize;

    Long total = mobeNodeMapper.countNodeList(
        userId,
        queryDTO.getKeyword(),
        queryDTO.getOwnerType());

    if (total == null || total == 0) {
      return PageResult.empty(pageNum, pageSize);
    }

    List<NodeListItemVO> list = mobeNodeMapper.selectNodeList(
        userId,
        queryDTO.getKeyword(),
        queryDTO.getOwnerType(),
        offset,
        pageSize);

    return PageResult.of(total, pageNum, pageSize, list);
  }

  /**
   * 获取节点详情。
   *
   * @param id 节点 ID。
   * @return 包含节点下待办简表的详情视图。
   */
  @Override
  public NodeDetailVO getNodeDetail(Long id) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    NodeDetailVO detailVO = mobeNodeMapper.selectNodeBaseDetail(id, userId);
    if (detailVO == null) {
      throw new BusinessException(NodeErrorCode.NODE_NOT_FOUND);
    }

    // 1. 查询节点下待办。节点详情页的核心信息就是“当前节点承接了哪些待办”。
    List<MobeTaskItem> taskList = mobeTaskItemMapper.selectList(
        new LambdaQueryWrapper<MobeTaskItem>()
            .eq(MobeTaskItem::getUserId, userId)
            .eq(MobeTaskItem::getDirectOwnerType, "NODE")
            .eq(MobeTaskItem::getDirectOwnerId, id)
            .eq(MobeTaskItem::getIsDeleted, 0)
            .orderByDesc(MobeTaskItem::getUpdateTime)
            .orderByDesc(MobeTaskItem::getId));

    // 2. 查询这些待办对应的状态。这里只加载实际被引用到的状态，避免无意义扫描整套模板。
    List<MobeTaskStatus> statusList = Collections.emptyList();
    List<Long> statusIds = taskList.stream()
        .map(MobeTaskItem::getCurrentStatusId)
        .filter(java.util.Objects::nonNull)
        .distinct()
        .toList();

    if (!statusIds.isEmpty()) {
      statusList = mobeTaskStatusMapper.selectList(
          new LambdaQueryWrapper<MobeTaskStatus>()
              .eq(MobeTaskStatus::getUserId, userId)
              .eq(MobeTaskStatus::getIsDeleted, 0)
              .in(MobeTaskStatus::getId, statusIds));
    }

    Map<Long, MobeTaskStatus> statusMap = statusList.stream()
        .collect(Collectors.toMap(MobeTaskStatus::getId, item -> item));

    // 3. 组装待办列表，把状态实体转换成页面更容易直接展示的文本视图。
    List<NodeTaskItemVO> taskVOList = taskList.stream().map(task -> {
      NodeTaskItemVO vo = new NodeTaskItemVO();
      vo.setId(task.getId());
      vo.setTitle(task.getTitle());
      vo.setDeadlineTime(task.getDeadlineTime());

      MobeTaskStatus status = statusMap.get(task.getCurrentStatusId());
      if (status != null) {
        vo.setStatusCode(status.getStatusCode());
        vo.setStatusText(status.getStatusName());
      }
      return vo;
    }).toList();

    detailVO.setTasks(taskVOList);
    return detailVO;
  }

  /**
   * 将节点标记为完成。
   *
   * @param id      节点 ID。
   * @param request 当前请求对象；当前版本未使用，但为后续审计日志预留入参。
   */
  @Override
  public void completeNode(Long id, HttpServletRequest request) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeNode node = mobeNodeMapper.selectById(id);
    if (node == null || Integer.valueOf(1).equals(node.getIsDeleted())) {
      throw new BusinessException(NodeErrorCode.NODE_NOT_FOUND);
    }

    if (!userId.equals(node.getUserId())) {
      throw new BusinessException(AuthErrorCode.NO_PERMISSION);
    }

    if (Integer.valueOf(1).equals(node.getIsCompleted())) {
      throw new BusinessException("节点已是完成状态");
    }
    // 只有当节点下所有待办都已经处于终态时，节点才允许进入完成态，避免出现“父级已完成、子任务未完成”的矛盾状态。
    List<MobeTaskItem> taskList = mobeTaskItemMapper.selectList(
        new LambdaQueryWrapper<MobeTaskItem>()
            .eq(MobeTaskItem::getUserId, userId)
            .eq(MobeTaskItem::getDirectOwnerType, "NODE")
            .eq(MobeTaskItem::getDirectOwnerId, id)
            .eq(MobeTaskItem::getIsDeleted, 0));

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
          throw new BusinessException("该节点下仍有未完成待办，不能赋予完成态");
        }
      }
    }
    node.setIsCompleted(1);
    node.setCompletedTime(LocalDateTime.now());
    node.setUpdatedBy(userId);

    mobeNodeMapper.updateById(node);
  }

  /**
   * 创建Node。
   *
   * @return 返回创建结果。
   */
  @Override
  public Long createNode(NodeCreateDTO dto) {
    log.info("MobeNodeServiceImpl createNode payload = {}", dto);
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    if (dto == null) {
      throw new BusinessException("请求参数不能为空");
    }

    String ownerType = dto.getOwnerType();
    Long ownerId = dto.getOwnerId();
    String title = dto.getTitle();

    if (!StringUtils.hasText(ownerType)) {
      throw new BusinessException("所属类型不能为空");
    }

    if (ownerId == null) {
      throw new BusinessException("所属对象不能为空");
    }

    if (!StringUtils.hasText(title)) {
      throw new BusinessException("节点名称不能为空");
    }

    ownerType = ownerType.trim().toUpperCase();

    if (!"PROJECT".equals(ownerType) && !"GOAL".equals(ownerType)) {
      throw new BusinessException("所属类型仅支持 PROJECT 或 GOAL");
    }

    checkOwnerExists(userId, ownerType, ownerId);

    MobeNode node = new MobeNode();
    node.setUserId(userId);
    node.setTitle(title.trim());
    node.setContent(dto.getContent());
    node.setRemark(dto.getRemark());
    node.setOwnerType(ownerType);
    node.setOwnerId(ownerId);
    node.setSortNo(0);
    node.setIsCompleted(0);
    node.setCompletedTime(null);
    node.setIsDeleted(0);
    node.setCreatedBy(userId);
    node.setUpdatedBy(userId);

    mobeNodeMapper.insert(node);
    return node.getId();
  }

  private void checkOwnerExists(Long userId, String ownerType, Long ownerId) {
    if ("PROJECT".equals(ownerType)) {
      MobeProject project = mobeProjectMapper.selectOne(
          new LambdaQueryWrapper<MobeProject>()
              .eq(MobeProject::getId, ownerId)
              .eq(MobeProject::getUserId, userId)
              .eq(MobeProject::getIsDeleted, 0)
              .last("LIMIT 1"));

      if (project == null) {
        throw new BusinessException(ProjectErrorCode.PROJECT_NOT_FOUND);
      }
      return;
    }

    if ("GOAL".equals(ownerType)) {
      MobeGoal goal = mobeGoalMapper.selectOne(
          new LambdaQueryWrapper<MobeGoal>()
              .eq(MobeGoal::getId, ownerId)
              .eq(MobeGoal::getUserId, userId)
              .eq(MobeGoal::getIsDeleted, 0)
              .last("LIMIT 1"));

      if (goal == null) {
        throw new BusinessException(GoalErrorCode.GOAL_NOT_FOUND);
      }
    }
  }

  /**
   * 更新Node。
   */
  @Override
  public void updateNode(Long id, NodeUpdateDTO dto) {
    log.info("MobeNodeServiceImpl updateNode id = {}, payload = {}", id, dto);

    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    if (dto == null) {
      throw new BusinessException("请求参数不能为空");
    }

    String title = dto.getTitle();
    if (!StringUtils.hasText(title)) {
      throw new BusinessException("节点名称不能为空");
    }

    MobeNode node = mobeNodeMapper.selectOne(
        new LambdaQueryWrapper<MobeNode>()
            .eq(MobeNode::getId, id)
            .eq(MobeNode::getUserId, userId)
            .eq(MobeNode::getIsDeleted, 0)
            .last("LIMIT 1"));

    if (node == null) {
      throw new BusinessException(NodeErrorCode.NODE_NOT_FOUND);
    }

    node.setTitle(title.trim());
    node.setContent(dto.getContent());
    node.setRemark(dto.getRemark());
    node.setUpdatedBy(userId);

    mobeNodeMapper.updateById(node);
  }

  /**
   * 删除Node。
   */
  @Override
  public void deleteNode(Long id, NodeDeleteDTO dto) {
    log.info("MobeNodeServiceImpl deleteNode id = {}, payload = {}", id, dto);

    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeNode node = mobeNodeMapper.selectOne(
        new LambdaQueryWrapper<MobeNode>()
            .eq(MobeNode::getId, id)
            .eq(MobeNode::getUserId, userId)
            .eq(MobeNode::getIsDeleted, 0)
            .last("LIMIT 1"));

    if (node == null) {
      throw new BusinessException(NodeErrorCode.NODE_NOT_FOUND);
    }

    List<MobeTaskItem> taskList = mobeTaskItemMapper.selectList(
        new LambdaQueryWrapper<MobeTaskItem>()
            .eq(MobeTaskItem::getUserId, userId)
            .eq(MobeTaskItem::getDirectOwnerType, "NODE")
            .eq(MobeTaskItem::getDirectOwnerId, id)
            .eq(MobeTaskItem::getIsDeleted, 0)
            .orderByAsc(MobeTaskItem::getId));

    if (taskList.isEmpty()) {
      doDeleteNode(node, userId);
      return;
    }

    String deleteMode = dto == null || !StringUtils.hasText(dto.getDeleteMode())
        ? null
        : dto.getDeleteMode().trim().toUpperCase(Locale.ROOT);
    if (deleteMode == null) {
      throw new BusinessException("当前节点中存在未完成的待办");
    }

    deleteMode = deleteMode.trim().toUpperCase();

    if ("COMPLETE_TASKS".equals(deleteMode)) {
      completeNodeTasks(taskList, userId);
      doDeleteNode(node, userId);
      return;
    }

    if ("DELETE_TASKS".equals(deleteMode)) {
      deleteNodeTasks(taskList, userId);
      doDeleteNode(node, userId);
      return;
    }

    throw new BusinessException("删除模式不支持");
  }

  private void doDeleteNode(MobeNode node, Long userId) {
    node.setIsDeleted(1);
    node.setUpdatedBy(userId);
    mobeNodeMapper.updateById(node);
  }

  private void completeNodeTasks(List<MobeTaskItem> taskList, Long userId) {
    if (taskList.isEmpty()) {
      return;
    }

    List<Long> statusIds = taskList.stream()
        .map(MobeTaskItem::getCurrentStatusId)
        .filter(java.util.Objects::nonNull)
        .distinct()
        .toList();

    List<MobeTaskStatus> currentStatusList = statusIds.isEmpty()
        ? Collections.emptyList()
        : mobeTaskStatusMapper.selectList(
            new LambdaQueryWrapper<MobeTaskStatus>()
                .eq(MobeTaskStatus::getUserId, userId)
                .eq(MobeTaskStatus::getIsDeleted, 0)
                .in(MobeTaskStatus::getId, statusIds));

    Map<Long, MobeTaskStatus> currentStatusMap = currentStatusList.stream()
        .collect(Collectors.toMap(MobeTaskStatus::getId, item -> item));

    List<Long> templateIds = currentStatusList.stream()
        .map(MobeTaskStatus::getTemplateId)
        .filter(java.util.Objects::nonNull)
        .distinct()
        .toList();

    List<MobeTaskStatus> templateStatusList = templateIds.isEmpty()
        ? Collections.emptyList()
        : mobeTaskStatusMapper.selectList(
            new LambdaQueryWrapper<MobeTaskStatus>()
                .eq(MobeTaskStatus::getUserId, userId)
                .eq(MobeTaskStatus::getIsDeleted, 0)
                .in(MobeTaskStatus::getTemplateId, templateIds));

    Map<Long, MobeTaskStatus> terminalStatusMap = templateStatusList.stream()
        .filter(status -> Integer.valueOf(1).equals(status.getIsTerminal()))
        .collect(Collectors.toMap(
            MobeTaskStatus::getTemplateId,
            status -> status,
            (a, b) -> a));

    LocalDateTime now = LocalDateTime.now();

    for (MobeTaskItem task : taskList) {
      MobeTaskStatus currentStatus = currentStatusMap.get(task.getCurrentStatusId());

      if (currentStatus != null && Integer.valueOf(1).equals(currentStatus.getIsTerminal())) {
        continue;
      }

      if (currentStatus == null || currentStatus.getTemplateId() == null) {
        throw new BusinessException("存在待办缺少有效状态模板，无法批量处理");
      }

      MobeTaskStatus terminalStatus = terminalStatusMap.get(currentStatus.getTemplateId());
      if (terminalStatus == null) {
        throw new BusinessException("存在待办未配置结束状态，无法批量处理");
      }

      task.setCurrentStatusId(terminalStatus.getId());
      task.setCompletedAt(now);
      task.setUpdatedBy(userId);
      mobeTaskItemMapper.updateById(task);
    }
  }

  private void deleteNodeTasks(List<MobeTaskItem> taskList, Long userId) {
    if (taskList.isEmpty()) {
      return;
    }

    for (MobeTaskItem task : taskList) {
      task.setIsDeleted(1);
      task.setUpdatedBy(userId);
      mobeTaskItemMapper.updateById(task);
    }
  }
}
