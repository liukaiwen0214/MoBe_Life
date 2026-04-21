package com.mobe.mobe_life_backend.node.service.impl;

import org.springframework.stereotype.Service;
import com.mobe.mobe_life_backend.node.service.MobeNodeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mobe.mobe_life_backend.common.context.UserContext;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.common.exception.AuthErrorCode;
import com.mobe.mobe_life_backend.common.exception.NodeErrorCode;
import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.node.dto.NodeListQueryDTO;
import com.mobe.mobe_life_backend.node.entity.MobeNode;
import com.mobe.mobe_life_backend.node.mapper.MobeNodeMapper;
import com.mobe.mobe_life_backend.node.vo.NodeDetailVO;
import com.mobe.mobe_life_backend.node.vo.NodeListItemVO;
import com.mobe.mobe_life_backend.node.vo.NodeTaskItemVO;
import com.mobe.mobe_life_backend.task.entity.MobeTaskItem;
import com.mobe.mobe_life_backend.task.entity.MobeTaskStatus;
import com.mobe.mobe_life_backend.task.mapper.MobeTaskItemMapper;
import com.mobe.mobe_life_backend.task.mapper.MobeTaskStatusMapper;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 节点服务实现类
 */
@Service
public class MobeNodeServiceImpl implements MobeNodeService {
  private final MobeNodeMapper mobeNodeMapper;
  private final MobeTaskItemMapper mobeTaskItemMapper;
  private final MobeTaskStatusMapper mobeTaskStatusMapper;

  public MobeNodeServiceImpl(MobeNodeMapper mobeNodeMapper,
      MobeTaskItemMapper mobeTaskItemMapper,
      MobeTaskStatusMapper mobeTaskStatusMapper) {
    this.mobeNodeMapper = mobeNodeMapper;
    this.mobeTaskItemMapper = mobeTaskItemMapper;
    this.mobeTaskStatusMapper = mobeTaskStatusMapper;
  }

  @Override
  public PageResult<NodeListItemVO> getNodeList(NodeListQueryDTO queryDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

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

    // 1. 查询节点下待办
    List<MobeTaskItem> taskList = mobeTaskItemMapper.selectList(
        new LambdaQueryWrapper<MobeTaskItem>()
            .eq(MobeTaskItem::getUserId, userId)
            .eq(MobeTaskItem::getDirectOwnerType, "NODE")
            .eq(MobeTaskItem::getDirectOwnerId, id)
            .eq(MobeTaskItem::getIsDeleted, 0)
            .orderByDesc(MobeTaskItem::getUpdateTime)
            .orderByDesc(MobeTaskItem::getId));

    // 2. 查询这些待办对应的状态
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

    // 3. 组装待办列表
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
}
