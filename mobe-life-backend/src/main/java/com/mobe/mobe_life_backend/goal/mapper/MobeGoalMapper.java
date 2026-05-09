/**
 * 声明目标中心的 Mapper，集中数据库访问入口。
 * 模块：目标中心 / 数据访问层。
 * 约束：通常由 MyBatis 或 MyBatis-Plus 生成代理实现，方法签名需与 XML 或框架约定对应。
 */
package com.mobe.mobe_life_backend.goal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mobe.mobe_life_backend.goal.entity.MobeGoal;
import org.apache.ibatis.annotations.Mapper;
import com.mobe.mobe_life_backend.goal.vo.GoalListItemVO;
import com.mobe.mobe_life_backend.goal.vo.GoalDetailVO;

import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MobeGoalMapper extends BaseMapper<MobeGoal> {
        List<GoalListItemVO> selectGoalList(
                        @Param("userId") Long userId,
                        @Param("keyword") String keyword,
                        @Param("includeCompleted") Boolean includeCompleted,
                        @Param("offset") long offset,
                        @Param("pageSize") int pageSize);

        Long countGoalList(
                        @Param("userId") Long userId,
                        @Param("keyword") String keyword,
                        @Param("includeCompleted") Boolean includeCompleted);

        GoalDetailVO selectGoalBaseDetail(@Param("id") Long id, @Param("userId") Long userId);
}
