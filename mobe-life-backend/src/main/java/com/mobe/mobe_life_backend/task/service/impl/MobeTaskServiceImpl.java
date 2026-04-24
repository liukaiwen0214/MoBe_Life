/**
 * 核心职责：聚合待办列表与详情查询，把任务、状态、操作日志和状态变更日志拼成前端可直接消费的视图。
 * 所属业务模块：待办中心 / 业务服务实现。
 * 重要依赖关系或外部约束：依赖 `UserContext` 提供当前用户、多个 Mapper 提供聚合查询；
 * 查询结果的正确性要求任务、状态、日志表之间的用户隔离条件保持一致。
 */
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
import com.mobe.mobe_life_backend.common.entity.MobeStatusChangeLog;
import com.mobe.mobe_life_backend.common.mapper.MobeStatusChangeLogMapper;
import com.mobe.mobe_life_backend.task.vo.TaskStatusChangeLogItemVO;
import com.mobe.mobe_life_backend.goal.entity.MobeGoal;
import com.mobe.mobe_life_backend.goal.mapper.MobeGoalMapper;
import com.mobe.mobe_life_backend.node.entity.MobeNode;
import com.mobe.mobe_life_backend.node.mapper.MobeNodeMapper;
import com.mobe.mobe_life_backend.project.entity.MobeProject;
import com.mobe.mobe_life_backend.project.mapper.MobeProjectMapper;
import com.mobe.mobe_life_backend.task.dto.TaskCreateDTO;
import com.mobe.mobe_life_backend.task.entity.MobeTaskItem;
import org.springframework.util.StringUtils;
import com.mobe.mobe_life_backend.task.dto.TaskNextStatusDTO;
import com.mobe.mobe_life_backend.task.dto.TaskUpdateDTO;
import com.mobe.mobe_life_backend.task.dto.TaskReleaseDTO;
import com.mobe.mobe_life_backend.task.vo.TaskFlowStatusItemVO;
import com.mobe.mobe_life_backend.task.vo.TaskFlowStatusOptionVO;
import com.mobe.mobe_life_backend.task.vo.TaskFlowVO;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

import java.util.Collections;
import java.util.List;

/**
 * 待办服务实现。
 *
 * <p>
 * 设计初衷是让控制层只关心“拿什么视图”，而把分页兜底、权限边界、状态字典拼装、
 * 操作日志裁剪等细节统一封装到这里。
 * </p>
 *
 * <p>
 * 线程安全性：本类作为 Spring 单例 Bean 使用，不保存请求级可变状态；
 * 每次调用都通过局部变量承载当前用户查询结果，因此线程安全。
 * </p>
 */
@Service
public class MobeTaskServiceImpl implements MobeTaskService {
  /** 待办主表访问入口。 */
  private final MobeTaskItemMapper mobeTaskItemMapper;
  /** 待办状态字典访问入口。 */
  private final MobeTaskStatusMapper mobeTaskStatusMapper;
  /** 待办操作日志访问入口。 */
  private final MobeTaskOperationLogMapper mobeTaskOperationLogMapper;
  /** 通用状态变更日志访问入口。 */
  private final MobeStatusChangeLogMapper mobeStatusChangeLogMapper;
  /** 项目访问入口。创建或修改待办时，如果归属到项目，需要从这里读取项目状态模板。 */
  private final MobeProjectMapper mobeProjectMapper;
  /** 目标访问入口。创建或修改待办时，如果归属到目标，需要从这里读取目标状态模板。 */
  private final MobeGoalMapper mobeGoalMapper;
  /** 节点访问入口。归属节点的待办需要先反查节点，再继续判断节点挂在哪个项目或目标下。 */
  private final MobeNodeMapper mobeNodeMapper;

  /**
   * 构造任务服务实现。
   *
   * <p>
   * 这里通过构造器注入依赖，是为了让这个服务在启动阶段就把必需组件准备齐，
   * 也方便测试和后续重构时看清楚它到底依赖哪些数据源。
   * </p>
   */
  public MobeTaskServiceImpl(MobeTaskItemMapper mobeTaskItemMapper,
      MobeTaskStatusMapper mobeTaskStatusMapper,
      MobeTaskOperationLogMapper mobeTaskOperationLogMapper,
      MobeStatusChangeLogMapper mobeStatusChangeLogMapper,
      MobeProjectMapper mobeProjectMapper,
      MobeGoalMapper mobeGoalMapper,
      MobeNodeMapper mobeNodeMapper) {
    this.mobeTaskItemMapper = mobeTaskItemMapper;
    this.mobeTaskStatusMapper = mobeTaskStatusMapper;
    this.mobeTaskOperationLogMapper = mobeTaskOperationLogMapper;
    this.mobeStatusChangeLogMapper = mobeStatusChangeLogMapper;
    this.mobeProjectMapper = mobeProjectMapper;
    this.mobeGoalMapper = mobeGoalMapper;
    this.mobeNodeMapper = mobeNodeMapper;
  }

  /**
   * 获取当前登录用户的待办列表。
   *
   * @param queryDTO 分页与筛选条件，不允许为 null。
   * @return 分页待办列表；当无数据时返回空分页对象，而不是 null。
   * @throws BusinessException 当用户未登录时抛出。
   */
  @Override
  public PageResult<TaskListItemVO> getTaskList(TaskListQueryDTO queryDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    // 对分页参数做服务层兜底，避免调用方遗漏页码时把无意义的空值传到 SQL 层。
    // 这里的策略很保守：非法页码直接改回默认值，而不是让数据库层再去容错。
    int pageNum = queryDTO.getPageNum() == null || queryDTO.getPageNum() < 1 ? 1 : queryDTO.getPageNum();
    int pageSize = queryDTO.getPageSize() == null || queryDTO.getPageSize() < 1 ? 10 : queryDTO.getPageSize();
    long offset = (long) (pageNum - 1) * pageSize;

    // 先查总数，再决定是否真的查列表数据。
    // 这样做的好处是：当没有数据时，可以直接返回空分页对象，减少一次无意义的列表查询。
    Long total = mobeTaskItemMapper.countTaskList(
        userId,
        queryDTO.getKeyword(),
        queryDTO.getStatusCode(),
        queryDTO.getDirectOwnerType());

    if (total == null || total == 0) {
      return PageResult.empty(pageNum, pageSize);
    }

    // 真正的列表查询只发生在 total > 0 时。
    List<TaskListItemVO> list = mobeTaskItemMapper.selectTaskList(
        userId,
        queryDTO.getKeyword(),
        queryDTO.getStatusCode(),
        queryDTO.getDirectOwnerType(),
        offset,
        pageSize);

    return PageResult.of(total, pageNum, pageSize, list);
  }

  /**
   * 获取单个待办详情。
   *
   * @param id 待办 ID。
   * @return 包含状态列表、操作日志、状态变更日志的详情视图。
   * @throws BusinessException 当用户未登录或待办不存在时抛出。
   */
  @Override
  public TaskDetailVO getTaskDetail(Long id) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    // 先拿到待办的“主干信息”。
    // 如果这一步就查不到，说明这个待办不存在，或者不属于当前登录用户。
    TaskDetailVO detailVO = mobeTaskItemMapper.selectTaskBaseDetail(id, userId);
    if (detailVO == null) {
      throw new BusinessException(TaskErrorCode.TASK_NOT_FOUND);
    }

    // 1. 查状态列表（按待办绑定的模板加载），让详情页可以直接绘制状态流转轨道。
    // 如果待办本身没有绑定状态模板，就保持空列表，前端据此知道当前没有可展示的状态流。
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
      // 这里做的是“实体 -> 视图对象”的转换。
      // 实体更偏数据库结构，VO 更偏前端展示结构，所以不会把整张状态表原样返回出去。
      vo.setId(status.getId());
      vo.setStatusName(status.getStatusName());
      vo.setStatusCode(status.getStatusCode());
      vo.setIsInitial(status.getIsInitial());
      vo.setIsTerminal(status.getIsTerminal());
      vo.setIsEnabled(status.getIsEnabled());
      return vo;
    }).toList();

    // 2. 查待办操作日志，只保留最近 20 条，避免详情页一次拉取过多历史数据。
    // 操作日志关注“有人对待办做了什么”，例如创建、编辑、删除附件等。
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
    // 3. 查待办状态变更日志。它与普通操作日志分开存，是为了保留更稳定的状态审计语义。
    // 简单理解：操作日志像“流水”，状态变更日志像“状态轨迹”。
    List<MobeStatusChangeLog> statusChangeLogs = mobeStatusChangeLogMapper.selectList(
        new LambdaQueryWrapper<MobeStatusChangeLog>()
            .eq(MobeStatusChangeLog::getUserId, userId)
            .eq(MobeStatusChangeLog::getBizType, "TASK")
            .eq(MobeStatusChangeLog::getBizId, id)
            .eq(MobeStatusChangeLog::getIsDeleted, 0)
            .orderByDesc(MobeStatusChangeLog::getCreateTime));

    List<TaskStatusChangeLogItemVO> statusChangeLogVOList = statusChangeLogs.stream().map(log -> {
      TaskStatusChangeLogItemVO vo = new TaskStatusChangeLogItemVO();
      vo.setId(log.getId());
      vo.setChangeType(log.getChangeType());
      vo.setFromStatusName(log.getFromStatusName());
      vo.setToStatusName(log.getToStatusName());
      vo.setChangeTime(log.getCreateTime());
      vo.setChangeRemark(log.getChangeRemark());
      return vo;
    }).toList();

    // 4. 回填聚合结果，让控制层一次返回完整详情，不再额外发起补充查询。
    // 到这里，这个详情对象已经不只是“待办主表的一行数据”，而是一个完整的详情页模型。
    detailVO.setStatusList(statusVOList);
    detailVO.setLogs(logVOList);
    detailVO.setStatusChangeLogs(statusChangeLogVOList);
    return detailVO;
  }

  /**
   * 创建待办。
   *
   * <p>
   * 这个方法做的事情并不只是“插入一条待办记录”，还包括：
   * 校验输入、确认归属对象是否合法、决定该待办应该挂哪套状态模板、
   * 并把待办初始化到模板规定的起始状态。
   * </p>
   *
   * @param dto 创建参数。
   * @return 新创建待办的主键 ID。
   * @throws BusinessException 当用户未登录、参数非法或归属对象不存在时抛出。
   */
  @Override
  public Long createTask(TaskCreateDTO dto) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    if (dto == null) {
      throw new BusinessException("请求参数不能为空");
    }

    String title = dto.getTitle();
    String directOwnerType = dto.getDirectOwnerType();

    if (!StringUtils.hasText(title)) {
      throw new BusinessException("待办标题不能为空");
    }

    if (!StringUtils.hasText(directOwnerType)) {
      throw new BusinessException("归属类型不能为空");
    }

    directOwnerType = directOwnerType.trim().toUpperCase();

    if (!isSupportedOwnerType(directOwnerType)) {
      throw new BusinessException("归属类型仅支持 INDEPENDENT、PROJECT、GOAL、NODE");
    }

    // 独立待办不隶属于项目、目标或节点，所以 directOwnerId 必须被清空。
    // 反过来讲，只要不是独立待办，就一定要知道它具体挂在哪个对象上。
    if ("INDEPENDENT".equals(directOwnerType)) {
      dto.setDirectOwnerId(null);
    } else if (dto.getDirectOwnerId() == null) {
      throw new BusinessException("归属对象不能为空");
    }

    // 计划开始时间和结束时间是成对语义。
    // 如果开始时间比结束时间还晚，说明前端或调用方传入的业务数据已经自相矛盾。
    if (dto.getPlanStartTime() != null && dto.getPlanEndTime() != null
        && dto.getPlanStartTime().isAfter(dto.getPlanEndTime())) {
      throw new BusinessException("计划开始时间不能晚于计划结束时间");
    }

    // 先根据归属对象反推出应该使用哪套状态模板，再从模板里找到“初始状态”。
    Long statusTemplateId = resolveStatusTemplateId(userId, directOwnerType, dto.getDirectOwnerId());
    MobeTaskStatus initialStatus = getInitialStatus(userId, statusTemplateId);

    MobeTaskItem task = new MobeTaskItem();
    // 下面这一段是在补齐一条“系统可接受的完整待办默认值”。
    // 因为数据库里的待办不仅有标题、备注这些业务字段，还有排序、来源、优先级、删除标记等系统字段。
    task.setUserId(userId);
    task.setTitle(title.trim());
    task.setContent(dto.getContent());
    task.setRemark(dto.getRemark());
    task.setDirectOwnerType(directOwnerType);
    task.setDirectOwnerId(dto.getDirectOwnerId());
    task.setStatusTemplateId(statusTemplateId);
    task.setCurrentStatusId(initialStatus.getId());
    task.setPlanStartTime(dto.getPlanStartTime());
    task.setPlanEndTime(dto.getPlanEndTime());
    task.setDeadlineTime(dto.getDeadlineTime());
    task.setActualStartTime(null);
    task.setCompletedAt(null);
    task.setArchivedAt(null);
    task.setRecordStatus("ACTIVE");
    task.setPriorityLevel("P2");
    task.setUrgencyLevel("MEDIUM");
    task.setUrgencySource("MANUAL");
    task.setSortNo(0);
    task.setIsPinned(0);
    task.setIsFocused(0);
    task.setSourceType("MANUAL");
    task.setSourceChannel("MINI_PROGRAM");
    task.setIsRepeatGenerated(0);
    task.setCreatedBy(userId);
    task.setUpdatedBy(userId);
    task.setIsDeleted(0);

    // 插入成功后，MyBatis-Plus 会把新生成的主键回填到 task 对象里。
    mobeTaskItemMapper.insert(task);
    return task.getId();
  }

  /**
   * 判断归属类型是否在系统支持范围内。
   *
   * @param directOwnerType 归属类型编码。
   * @return 支持返回 true，否则返回 false。
   */
  private boolean isSupportedOwnerType(String directOwnerType) {
    return "INDEPENDENT".equals(directOwnerType)
        || "PROJECT".equals(directOwnerType)
        || "GOAL".equals(directOwnerType)
        || "NODE".equals(directOwnerType);
  }

  /**
   * 根据归属对象反推出待办应该绑定的状态模板。
   *
   * <p>
   * 这一步是整个待办模型里很关键的规则：待办的状态不是随便指定的，
   * 而是继承自它所挂载的项目、目标，或者节点的上级对象。
   * </p>
   *
   * @param userId          当前登录用户 ID。
   * @param directOwnerType 归属类型。
   * @param directOwnerId   归属对象 ID。
   * @return 可用的状态模板 ID。
   */
  private Long resolveStatusTemplateId(Long userId, String directOwnerType, Long directOwnerId) {
    if ("INDEPENDENT".equals(directOwnerType)) {
      // 独立待办没有上级对象，因此只能从“当前用户可用的默认初始状态”反推出模板。
      MobeTaskStatus initialStatus = mobeTaskStatusMapper.selectOne(
          new LambdaQueryWrapper<MobeTaskStatus>()
              .eq(MobeTaskStatus::getUserId, userId)
              .eq(MobeTaskStatus::getIsInitial, 1)
              .eq(MobeTaskStatus::getIsDeleted, 0)
              .orderByAsc(MobeTaskStatus::getTemplateId)
              .orderByAsc(MobeTaskStatus::getSortNo)
              .last("LIMIT 1"));

      if (initialStatus == null || initialStatus.getTemplateId() == null) {
        throw new BusinessException("未找到可用的默认状态模板");
      }
      return initialStatus.getTemplateId();
    }

    if ("PROJECT".equals(directOwnerType)) {
      // 挂在项目下的待办，直接继承项目绑定的状态模板。
      MobeProject project = mobeProjectMapper.selectOne(
          new LambdaQueryWrapper<MobeProject>()
              .eq(MobeProject::getId, directOwnerId)
              .eq(MobeProject::getUserId, userId)
              .eq(MobeProject::getIsDeleted, 0)
              .last("LIMIT 1"));

      if (project == null) {
        throw new BusinessException("所属项目不存在");
      }
      if (project.getStatusTemplateId() == null) {
        throw new BusinessException("所属项目未绑定状态模板");
      }
      return project.getStatusTemplateId();
    }

    if ("GOAL".equals(directOwnerType)) {
      // 挂在目标下的待办，直接继承目标绑定的状态模板。
      MobeGoal goal = mobeGoalMapper.selectOne(
          new LambdaQueryWrapper<MobeGoal>()
              .eq(MobeGoal::getId, directOwnerId)
              .eq(MobeGoal::getUserId, userId)
              .eq(MobeGoal::getIsDeleted, 0)
              .last("LIMIT 1"));

      if (goal == null) {
        throw new BusinessException("所属目标不存在");
      }
      if (goal.getStatusTemplateId() == null) {
        throw new BusinessException("所属目标未绑定状态模板");
      }
      return goal.getStatusTemplateId();
    }

    // 剩下的情况就是 NODE。
    // 节点本身不一定直接持有状态模板，所以还要继续往上追它到底挂在项目还是目标下。
    MobeNode node = mobeNodeMapper.selectOne(
        new LambdaQueryWrapper<MobeNode>()
            .eq(MobeNode::getId, directOwnerId)
            .eq(MobeNode::getUserId, userId)
            .eq(MobeNode::getIsDeleted, 0)
            .last("LIMIT 1"));

    if (node == null) {
      throw new BusinessException("所属节点不存在");
    }

    String ownerType = node.getOwnerType();
    Long ownerId = node.getOwnerId();

    if (!StringUtils.hasText(ownerType) || ownerId == null) {
      throw new BusinessException("所属节点缺少有效归属信息");
    }

    ownerType = ownerType.trim().toUpperCase();

    if ("PROJECT".equals(ownerType)) {
      // 节点挂在项目下，则复用项目状态模板。
      MobeProject project = mobeProjectMapper.selectOne(
          new LambdaQueryWrapper<MobeProject>()
              .eq(MobeProject::getId, ownerId)
              .eq(MobeProject::getUserId, userId)
              .eq(MobeProject::getIsDeleted, 0)
              .last("LIMIT 1"));

      if (project == null) {
        throw new BusinessException("节点所属项目不存在");
      }
      if (project.getStatusTemplateId() == null) {
        throw new BusinessException("节点所属项目未绑定状态模板");
      }
      return project.getStatusTemplateId();
    }

    if ("GOAL".equals(ownerType)) {
      // 节点挂在目标下，则复用目标状态模板。
      MobeGoal goal = mobeGoalMapper.selectOne(
          new LambdaQueryWrapper<MobeGoal>()
              .eq(MobeGoal::getId, ownerId)
              .eq(MobeGoal::getUserId, userId)
              .eq(MobeGoal::getIsDeleted, 0)
              .last("LIMIT 1"));

      if (goal == null) {
        throw new BusinessException("节点所属目标不存在");
      }
      if (goal.getStatusTemplateId() == null) {
        throw new BusinessException("节点所属目标未绑定状态模板");
      }
      return goal.getStatusTemplateId();
    }

    throw new BusinessException("节点归属类型不支持");
  }

  /**
   * 读取某个状态模板的初始状态。
   *
   * @param userId           当前登录用户 ID。
   * @param statusTemplateId 状态模板 ID。
   * @return 模板中的初始状态。
   */
  private MobeTaskStatus getInitialStatus(Long userId, Long statusTemplateId) {
    MobeTaskStatus initialStatus = mobeTaskStatusMapper.selectOne(
        new LambdaQueryWrapper<MobeTaskStatus>()
            .eq(MobeTaskStatus::getUserId, userId)
            .eq(MobeTaskStatus::getTemplateId, statusTemplateId)
            .eq(MobeTaskStatus::getIsInitial, 1)
            .eq(MobeTaskStatus::getIsDeleted, 0)
            .orderByAsc(MobeTaskStatus::getSortNo)
            .orderByAsc(MobeTaskStatus::getId)
            .last("LIMIT 1"));

    if (initialStatus == null) {
      throw new BusinessException("当前状态模板未配置初始状态");
    }
    return initialStatus;
  }

  /**
   * 将待办推进到下一个状态。
   *
   * <p>
   * 这里采用的是“顺序推进”模型：当前状态必须能在状态模板里找到，
   * 并且只能移动到排序上的下一个启用状态，而不是任意跳转。
   * </p>
   *
   * @param id  待办 ID。
   * @param dto 状态推进附加参数，例如备注。
   */
  @Override
  public void moveTaskToNextStatus(Long id, TaskNextStatusDTO dto) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeTaskItem task = mobeTaskItemMapper.selectOne(
        new LambdaQueryWrapper<MobeTaskItem>()
            .eq(MobeTaskItem::getId, id)
            .eq(MobeTaskItem::getUserId, userId)
            .eq(MobeTaskItem::getIsDeleted, 0)
            .last("LIMIT 1"));

    if (task == null) {
      throw new BusinessException(TaskErrorCode.TASK_NOT_FOUND);
    }

    if (task.getStatusTemplateId() == null) {
      throw new BusinessException("当前待办未绑定状态模板");
    }

    if (task.getCurrentStatusId() == null) {
      throw new BusinessException("当前待办缺少有效状态");
    }

    // 把模板里当前启用的状态完整拉出来，后面才能判断“当前状态是谁、下一个状态是谁”。
    List<MobeTaskStatus> statusList = mobeTaskStatusMapper.selectList(
        new LambdaQueryWrapper<MobeTaskStatus>()
            .eq(MobeTaskStatus::getUserId, userId)
            .eq(MobeTaskStatus::getTemplateId, task.getStatusTemplateId())
            .eq(MobeTaskStatus::getIsDeleted, 0)
            .eq(MobeTaskStatus::getIsEnabled, 1)
            .orderByAsc(MobeTaskStatus::getSortNo)
            .orderByAsc(MobeTaskStatus::getId));

    if (statusList.isEmpty()) {
      throw new BusinessException("当前状态模板未配置可用状态");
    }

    Map<Long, MobeTaskStatus> statusMap = statusList.stream()
        .collect(Collectors.toMap(MobeTaskStatus::getId, Function.identity()));

    // 先确认当前待办记录里保存的 currentStatusId 确实还存在于模板中。
    MobeTaskStatus currentStatus = statusMap.get(task.getCurrentStatusId());
    if (currentStatus == null) {
      throw new BusinessException("当前待办状态不在可用状态模板中");
    }

    if (Integer.valueOf(1).equals(currentStatus.getIsTerminal())) {
      throw new BusinessException("当前待办已处于结束状态");
    }

    // 这里不用数据库直接查“下一个状态”，而是先在排好序的列表里找到当前位置，
    // 再取下一个元素。这样可以让“状态推进顺序”完全由模板排序控制。
    int currentIndex = -1;
    for (int i = 0; i < statusList.size(); i++) {
      if (task.getCurrentStatusId().equals(statusList.get(i).getId())) {
        currentIndex = i;
        break;
      }
    }

    if (currentIndex == -1) {
      throw new BusinessException("当前待办状态不在可用状态模板中");
    }

    if (currentIndex >= statusList.size() - 1) {
      throw new BusinessException("当前待办已无下一个状态");
    }

    MobeTaskStatus nextStatus = statusList.get(currentIndex + 1);
    LocalDateTime now = LocalDateTime.now();

    // 真正更新待办主记录。
    task.setCurrentStatusId(nextStatus.getId());

    // 首次进入流转时，把“实际开始时间”补上。
    // 这样后续即使多次推进状态，也能保留第一次真正开始执行的时间点。
    if (task.getActualStartTime() == null) {
      task.setActualStartTime(now);
    }

    // 如果进入的是终态，就顺手写入完成时间；否则清空完成时间，避免状态和完成时间互相矛盾。
    if (Integer.valueOf(1).equals(nextStatus.getIsTerminal())) {
      task.setCompletedAt(now);
    } else {
      task.setCompletedAt(null);
    }

    task.setUpdatedBy(userId);
    mobeTaskItemMapper.updateById(task);

    // 主表更新完成后，再追加一条状态变更日志，形成可追溯的状态历史。
    MobeStatusChangeLog log = new MobeStatusChangeLog();
    log.setUserId(userId);
    log.setBizType("TASK");
    log.setBizId(task.getId());
    log.setStatusTemplateId(task.getStatusTemplateId());
    log.setChangeType("STATUS_CHANGE");
    log.setFromStatusId(currentStatus.getId());
    log.setFromStatusCode(currentStatus.getStatusCode());
    log.setFromStatusName(currentStatus.getStatusName());
    log.setToStatusId(nextStatus.getId());
    log.setToStatusCode(nextStatus.getStatusCode());
    log.setToStatusName(nextStatus.getStatusName());
    log.setChangeRemark(dto == null ? null : dto.getChangeRemark());
    log.setOperatorId(userId);
    log.setIsDeleted(0);

    mobeStatusChangeLogMapper.insert(log);
  }

  /**
   * 更新待办基础信息。
   *
   * <p>
   * 这个方法除了改标题、内容、时间外，还有一个关键规则：
   * 如果归属对象发生变化，导致状态模板切换，就必须把待办状态重置到新模板的初始状态。
   * </p>
   *
   * @param id  待办 ID。
   * @param dto 更新参数。
   */
  @Override
  public void updateTask(Long id, TaskUpdateDTO dto) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    if (dto == null) {
      throw new BusinessException("请求参数不能为空");
    }

    String title = dto.getTitle();
    String directOwnerType = dto.getDirectOwnerType();

    if (!StringUtils.hasText(title)) {
      throw new BusinessException("待办标题不能为空");
    }

    if (!StringUtils.hasText(directOwnerType)) {
      throw new BusinessException("归属类型不能为空");
    }

    directOwnerType = directOwnerType.trim().toUpperCase();

    if (!isSupportedOwnerType(directOwnerType)) {
      throw new BusinessException("归属类型仅支持 INDEPENDENT、PROJECT、GOAL、NODE");
    }

    // 更新时也沿用创建时的归属规则，确保数据语义始终一致。
    if ("INDEPENDENT".equals(directOwnerType)) {
      dto.setDirectOwnerId(null);
    } else if (dto.getDirectOwnerId() == null) {
      throw new BusinessException("归属对象不能为空");
    }

    if (dto.getPlanStartTime() != null && dto.getPlanEndTime() != null
        && dto.getPlanStartTime().isAfter(dto.getPlanEndTime())) {
      throw new BusinessException("计划开始时间不能晚于计划结束时间");
    }

    MobeTaskItem task = mobeTaskItemMapper.selectOne(
        new LambdaQueryWrapper<MobeTaskItem>()
            .eq(MobeTaskItem::getId, id)
            .eq(MobeTaskItem::getUserId, userId)
            .eq(MobeTaskItem::getIsDeleted, 0)
            .last("LIMIT 1"));

    if (task == null) {
      throw new BusinessException(TaskErrorCode.TASK_NOT_FOUND);
    }

    // 旧模板和新模板不一样，意味着这个待办已经被“搬家”到另一套状态流里了。
    Long oldTemplateId = task.getStatusTemplateId();
    Long newTemplateId = resolveStatusTemplateId(userId, directOwnerType, dto.getDirectOwnerId());

    task.setTitle(title.trim());
    task.setContent(dto.getContent());
    task.setRemark(dto.getRemark());
    task.setDirectOwnerType(directOwnerType);
    task.setDirectOwnerId(dto.getDirectOwnerId());
    task.setPlanStartTime(dto.getPlanStartTime());
    task.setPlanEndTime(dto.getPlanEndTime());
    task.setDeadlineTime(dto.getDeadlineTime());
    task.setStatusTemplateId(newTemplateId);

    if (!Objects.equals(oldTemplateId, newTemplateId)) {
      // 模板切换后，旧状态在新模板里未必存在，所以直接回到新模板的初始状态最安全、语义也最清晰。
      MobeTaskStatus initialStatus = getInitialStatus(userId, newTemplateId);
      task.setCurrentStatusId(initialStatus.getId());
      task.setActualStartTime(null);
      task.setCompletedAt(null);
    }

    task.setUpdatedBy(userId);
    mobeTaskItemMapper.updateById(task);
  }

  /**
   * 软删除待办。
   *
   * <p>
   * 这里没有直接物理删除数据库记录，而是把 `isDeleted` 标记为 1。
   * 这样做的好处是：后续若需要审计、恢复或追溯历史，原始数据还在。
   * </p>
   *
   * @param id 待办 ID。
   */
  @Override
  public void deleteTask(Long id) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeTaskItem task = mobeTaskItemMapper.selectOne(
        new LambdaQueryWrapper<MobeTaskItem>()
            .eq(MobeTaskItem::getId, id)
            .eq(MobeTaskItem::getUserId, userId)
            .eq(MobeTaskItem::getIsDeleted, 0)
            .last("LIMIT 1"));

    if (task == null) {
      throw new BusinessException(TaskErrorCode.TASK_NOT_FOUND);
    }

    // 软删除只改标记位，不真正删库。
    task.setIsDeleted(1);
    task.setUpdatedBy(userId);

    mobeTaskItemMapper.updateById(task);
  }

  @Override
  public TaskFlowVO getTaskFlow(Long id) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeTaskItem task = mobeTaskItemMapper.selectOne(
        new LambdaQueryWrapper<MobeTaskItem>()
            .eq(MobeTaskItem::getId, id)
            .eq(MobeTaskItem::getUserId, userId)
            .eq(MobeTaskItem::getIsDeleted, 0)
            .last("LIMIT 1"));

    if (task == null) {
      throw new BusinessException(TaskErrorCode.TASK_NOT_FOUND);
    }

    if (task.getStatusTemplateId() == null || task.getCurrentStatusId() == null) {
      throw new BusinessException("当前待办缺少有效流程状态信息");
    }

    List<MobeTaskStatus> statusList = mobeTaskStatusMapper.selectList(
        new LambdaQueryWrapper<MobeTaskStatus>()
            .eq(MobeTaskStatus::getUserId, userId)
            .eq(MobeTaskStatus::getTemplateId, task.getStatusTemplateId())
            .eq(MobeTaskStatus::getIsDeleted, 0)
            .orderByAsc(MobeTaskStatus::getSortNo)
            .orderByAsc(MobeTaskStatus::getId));

    if (statusList.isEmpty()) {
      throw new BusinessException("当前状态模板未配置流程状态");
    }

    Map<Long, MobeTaskStatus> statusMap = statusList.stream()
        .collect(Collectors.toMap(MobeTaskStatus::getId, Function.identity()));

    MobeTaskStatus currentStatus = statusMap.get(task.getCurrentStatusId());
    if (currentStatus == null) {
      throw new BusinessException("当前待办状态不在模板中");
    }

    int currentIndex = -1;
    for (int i = 0; i < statusList.size(); i++) {
      if (Objects.equals(statusList.get(i).getId(), currentStatus.getId())) {
        currentIndex = i;
        break;
      }
    }

    TaskFlowStatusOptionVO nextStatus = null;
    if (currentIndex >= 0 && currentIndex < statusList.size() - 1) {
      MobeTaskStatus next = statusList.get(currentIndex + 1);
      if (Integer.valueOf(1).equals(next.getIsEnabled())) {
        nextStatus = buildFlowOption(next);
      }
    }

    List<TaskFlowStatusOptionVO> releaseOptions = statusList.stream()
        .filter(item -> Integer.valueOf(1).equals(item.getIsEnabled()))
        .filter(item -> !Integer.valueOf(1).equals(item.getIsTerminal()))
        .map(this::buildFlowOption)
        .toList();

    List<TaskFlowStatusItemVO> statusItemVOList = statusList.stream().map(item -> {
      TaskFlowStatusItemVO vo = new TaskFlowStatusItemVO();
      vo.setId(item.getId());
      vo.setStatusCode(item.getStatusCode());
      vo.setStatusName(item.getStatusName());
      vo.setSortNo(item.getSortNo());
      vo.setIsInitial(item.getIsInitial());
      vo.setIsTerminal(item.getIsTerminal());
      vo.setIsEnabled(item.getIsEnabled());
      vo.setStatusColor(item.getStatusColor());
      vo.setStatusIcon(item.getStatusIcon());
      return vo;
    }).toList();

    TaskFlowVO vo = new TaskFlowVO();
    vo.setTaskId(task.getId());
    vo.setCurrentStatusId(currentStatus.getId());
    vo.setCurrentStatusCode(currentStatus.getStatusCode());
    vo.setCurrentStatusName(currentStatus.getStatusName());
    vo.setIsTerminal(currentStatus.getIsTerminal());
    vo.setAllowNext(
        !Integer.valueOf(1).equals(currentStatus.getIsTerminal()) && nextStatus != null ? 1 : 0);
    vo.setAllowRelease(
        Integer.valueOf(1).equals(currentStatus.getIsTerminal())
            && Integer.valueOf(1).equals(currentStatus.getAllowReopen())
                ? 1
                : 0);
    vo.setNextStatus(nextStatus);
    vo.setReleaseOptions(releaseOptions);
    vo.setStatusList(statusItemVOList);
    return vo;
  }

  private TaskFlowStatusOptionVO buildFlowOption(MobeTaskStatus status) {
    TaskFlowStatusOptionVO vo = new TaskFlowStatusOptionVO();
    vo.setId(status.getId());
    vo.setStatusCode(status.getStatusCode());
    vo.setStatusName(status.getStatusName());
    return vo;
  }

  @Override
  public void releaseTaskToStatus(Long id, TaskReleaseDTO dto) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    if (dto == null || dto.getTargetStatusId() == null) {
      throw new BusinessException("目标状态不能为空");
    }

    MobeTaskItem task = mobeTaskItemMapper.selectOne(
        new LambdaQueryWrapper<MobeTaskItem>()
            .eq(MobeTaskItem::getId, id)
            .eq(MobeTaskItem::getUserId, userId)
            .eq(MobeTaskItem::getIsDeleted, 0)
            .last("LIMIT 1"));

    if (task == null) {
      throw new BusinessException(TaskErrorCode.TASK_NOT_FOUND);
    }

    if (task.getStatusTemplateId() == null || task.getCurrentStatusId() == null) {
      throw new BusinessException("当前待办缺少有效流程状态信息");
    }

    List<MobeTaskStatus> statusList = mobeTaskStatusMapper.selectList(
        new LambdaQueryWrapper<MobeTaskStatus>()
            .eq(MobeTaskStatus::getUserId, userId)
            .eq(MobeTaskStatus::getTemplateId, task.getStatusTemplateId())
            .eq(MobeTaskStatus::getIsDeleted, 0)
            .orderByAsc(MobeTaskStatus::getSortNo)
            .orderByAsc(MobeTaskStatus::getId));

    if (statusList.isEmpty()) {
      throw new BusinessException("当前状态模板未配置流程状态");
    }

    Map<Long, MobeTaskStatus> statusMap = statusList.stream()
        .collect(Collectors.toMap(MobeTaskStatus::getId, Function.identity()));

    MobeTaskStatus currentStatus = statusMap.get(task.getCurrentStatusId());
    if (currentStatus == null) {
      throw new BusinessException("当前待办状态不在模板中");
    }

    if (!Integer.valueOf(1).equals(currentStatus.getIsTerminal())) {
      throw new BusinessException("当前待办未处于终态，不能放出");
    }

    if (!Integer.valueOf(1).equals(currentStatus.getAllowReopen())) {
      throw new BusinessException("当前终态不允许放出");
    }

    MobeTaskStatus targetStatus = statusMap.get(dto.getTargetStatusId());
    if (targetStatus == null) {
      throw new BusinessException("目标状态不存在或不属于当前模板");
    }

    if (!Integer.valueOf(1).equals(targetStatus.getIsEnabled())) {
      throw new BusinessException("目标状态未启用");
    }

    if (Integer.valueOf(1).equals(targetStatus.getIsTerminal())) {
      throw new BusinessException("目标状态不能是终态");
    }

    if (Objects.equals(currentStatus.getId(), targetStatus.getId())) {
      throw new BusinessException("当前已处于该状态");
    }

    task.setCurrentStatusId(targetStatus.getId());
    task.setCompletedAt(null);
    task.setUpdatedBy(userId);
    mobeTaskItemMapper.updateById(task);

    MobeStatusChangeLog log = new MobeStatusChangeLog();
    log.setUserId(userId);
    log.setBizType("TASK");
    log.setBizId(task.getId());
    log.setStatusTemplateId(task.getStatusTemplateId());
    log.setFromStatusId(currentStatus.getId());
    log.setFromStatusCode(currentStatus.getStatusCode());
    log.setFromStatusName(currentStatus.getStatusName());
    log.setToStatusId(targetStatus.getId());
    log.setToStatusCode(targetStatus.getStatusCode());
    log.setToStatusName(targetStatus.getStatusName());
    log.setChangeType("REOPEN");
    log.setChangeRemark(dto.getChangeRemark());
    log.setOperatorId(userId);
    log.setIsDeleted(0);
    mobeStatusChangeLogMapper.insert(log);
  }
}
