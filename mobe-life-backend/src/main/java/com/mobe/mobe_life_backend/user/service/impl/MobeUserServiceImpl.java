/**
 * 核心职责：实现当前登录用户资料读取与更新逻辑。
 * 所属业务模块：用户中心 / 业务服务实现。
 * 重要依赖关系或外部约束：依赖 `UserContext` 获取当前身份，依赖 `mobe_user` 表维护用户资料。
 */
package com.mobe.mobe_life_backend.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobe.mobe_life_backend.common.context.UserContext;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.user.dto.UpdateUserProfileDTO;
import com.mobe.mobe_life_backend.user.entity.MobeUser;
import com.mobe.mobe_life_backend.user.mapper.MobeUserMapper;
import com.mobe.mobe_life_backend.user.service.MobeUserService;
import com.mobe.mobe_life_backend.user.vo.CurrentUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现。
 *
 * <p>设计初衷是把“当前登录用户”视角下的资料操作封装起来，
 * 让控制层看到的是稳定 VO，而不是直接暴露数据库实体。</p>
 *
 * <p>线程安全性：无共享可变状态，作为 Spring 单例 Bean 使用安全。</p>
 */
@Service
public class MobeUserServiceImpl extends ServiceImpl<MobeUserMapper, MobeUser> implements MobeUserService {

  /**
   * 获取当前登录用户资料。
   *
   * @return 当前用户视图，不返回 null。
   * @throws BusinessException 当未登录或用户不存在时抛出。
   */
  @Override
  public CurrentUserVO getCurrentUser() {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException("当前用户未登录");
    }

    MobeUser user = this.getById(userId);
    if (user == null || Integer.valueOf(1).equals(user.getIsDeleted())) {
      throw new BusinessException("用户不存在");
    }

    CurrentUserVO currentUserVO = new CurrentUserVO();
    BeanUtils.copyProperties(user, currentUserVO);

    if (user.getBirthday() != null) {
      currentUserVO.setBirthday(user.getBirthday().toString());
    }
    currentUserVO.setHasPassword(user.getPassword() != null && !user.getPassword().isBlank());
    currentUserVO.setHasPhone(user.getPhone() != null && !user.getPhone().isBlank());
    currentUserVO.setHasEmail(user.getEmail() != null && !user.getEmail().isBlank());

    return currentUserVO;
  }

  /**
   * 更新当前登录用户资料。
   *
   * @param updateUserProfileDTO 更新参数，不允许为 null。
   * @throws BusinessException 当未登录或用户不存在时抛出。
   * @implNote 当前采用“非 null 才覆盖”的策略，因此无法通过传 null 清空已有字段。
   */
  @Override
  public void updateCurrentUserProfile(UpdateUserProfileDTO updateUserProfileDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException("当前用户未登录");
    }

    MobeUser user = this.getById(userId);
    if (user == null || Integer.valueOf(1).equals(user.getIsDeleted())) {
      throw new BusinessException("用户不存在");
    }

    if (updateUserProfileDTO.getNickname() != null) {
      user.setNickname(updateUserProfileDTO.getNickname());
    }
    if (updateUserProfileDTO.getAvatar() != null) {
      user.setAvatar(updateUserProfileDTO.getAvatar());
    }
    if (updateUserProfileDTO.getGender() != null) {
      user.setGender(updateUserProfileDTO.getGender());
    }
    if (updateUserProfileDTO.getBirthday() != null) {
      user.setBirthday(updateUserProfileDTO.getBirthday());
    }

    this.updateById(user);
  }
}
