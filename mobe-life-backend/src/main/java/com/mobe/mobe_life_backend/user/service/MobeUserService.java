/**
 * 核心职责：定义用户中心围绕“当前用户”展开的业务能力。
 * 所属业务模块：用户中心 / 业务服务接口。
 * 重要依赖关系或外部约束：继承 MyBatis Plus `IService` 以复用基础 CRUD，同时补充当前项目的业务方法。
 */
package com.mobe.mobe_life_backend.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mobe.mobe_life_backend.user.dto.UpdateUserProfileDTO;
import com.mobe.mobe_life_backend.user.entity.MobeUser;
import com.mobe.mobe_life_backend.user.vo.CurrentUserVO;

/**
 * 用户服务接口。
 *
 * <p>设计初衷是区分“通用用户数据操作”和“围绕当前登录用户的业务动作”，
 * 让控制层无需直接理解 `UserContext` 或实体转换细节。</p>
 */
public interface MobeUserService extends IService<MobeUser> {

  /**
   * 获取当前登录用户资料。
   *
   * @return 当前用户视图，不返回 null。
   * @throws RuntimeException 当当前请求未登录或用户不存在时抛出。
   */
  CurrentUserVO getCurrentUser();

  /**
   * 更新当前登录用户资料。
   *
   * @param updateUserProfileDTO 更新参数，不允许为 null；非 null 字段会被写回用户资料。
   * @throws RuntimeException 当当前请求未登录、用户不存在或数据不满足业务约束时抛出。
   */
  void updateCurrentUserProfile(UpdateUserProfileDTO updateUserProfileDTO);
}
