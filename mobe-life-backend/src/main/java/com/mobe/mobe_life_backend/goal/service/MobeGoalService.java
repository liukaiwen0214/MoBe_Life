/**
 * 核心职责：定义目标中心的业务能力边界，为控制层提供稳定调用接口。
 * 所属业务模块：目标中心 / 服务接口。
 * 重要依赖关系或外部约束：实现通常位于 `impl` 包中，接口方法语义应尽量稳定。
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
