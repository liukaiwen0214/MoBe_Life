/**
 * 核心职责：声明待办中心的数据访问接口，负责组织数据库查询与持久化入口。
 * 所属业务模块：待办中心 / 数据访问层。
 * 重要依赖关系或外部约束：通常由 MyBatis 或 MyBatis-Plus 生成代理实现，方法签名需与 XML 或框架约定对应。
 */
package com.mobe.mobe_life_backend.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mobe.mobe_life_backend.task.entity.MobeTaskItem;
import org.apache.ibatis.annotations.Mapper;
import com.mobe.mobe_life_backend.task.vo.TaskDetailVO;
import com.mobe.mobe_life_backend.task.vo.TaskListItemVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MobeTaskItemMapper extends BaseMapper<MobeTaskItem> {
  List<TaskListItemVO> selectTaskList(@Param("userId") Long userId,
      @Param("keyword") String keyword,
      @Param("statusCode") String statusCode,
      @Param("directOwnerType") String directOwnerType,
      @Param("offset") Long offset,
      @Param("pageSize") Integer pageSize);

  Long countTaskList(@Param("userId") Long userId,
      @Param("keyword") String keyword,
      @Param("statusCode") String statusCode,
      @Param("directOwnerType") String directOwnerType);

  TaskDetailVO selectTaskBaseDetail(@Param("id") Long id, @Param("userId") Long userId);
}
