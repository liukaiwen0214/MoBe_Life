/**
 * 界定目标中心的业务能力边界，供控制层稳定调用。
 * 模块：目标中心 / 服务接口。
 * 约束：实现通常位于 `impl` 包中，接口方法语义应尽量稳定。
 */
package com.mobe.mobe_life_backend.goal.service;

import com.mobe.mobe_life_backend.common.response.PageResult;
import com.mobe.mobe_life_backend.goal.dto.GoalListQueryDTO;
import com.mobe.mobe_life_backend.goal.vo.GoalDetailVO;
import com.mobe.mobe_life_backend.goal.vo.GoalListItemVO;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 目标服务接口
 */
public interface MobeGoalService {
  PageResult<GoalListItemVO> getGoalList(GoalListQueryDTO queryDTO);

  GoalDetailVO getGoalDetail(Long id);

  void completeGoal(Long id, HttpServletRequest request);

  void reopenGoal(Long id);

  void reopenGoalWithNodes(Long id);
}
