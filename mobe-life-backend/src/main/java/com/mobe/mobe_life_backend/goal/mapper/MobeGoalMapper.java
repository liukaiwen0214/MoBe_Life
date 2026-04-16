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
  List<GoalListItemVO> selectGoalList(@Param("userId") Long userId,
      @Param("keyword") String keyword,
      @Param("statusCode") String statusCode,
      @Param("offset") Long offset,
      @Param("pageSize") Integer pageSize);

  Long countGoalList(@Param("userId") Long userId,
      @Param("keyword") String keyword,
      @Param("statusCode") String statusCode);

  GoalDetailVO selectGoalBaseDetail(@Param("id") Long id, @Param("userId") Long userId);
}
