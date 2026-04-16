package com.mobe.mobe_life_backend.goal.service;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.goal.dto.GoalListQueryDTO;
import com.mobe.mobe_life_backend.goal.vo.GoalDetailVO;
import com.mobe.mobe_life_backend.goal.vo.GoalListItemVO;

/**
 * 目标服务接口
 */
public interface MobeGoalService {
  PageResult<GoalListItemVO> getGoalList(GoalListQueryDTO queryDTO);

  GoalDetailVO getGoalDetail(Long id);
}
