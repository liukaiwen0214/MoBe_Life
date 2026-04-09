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

@Service
public class MobeUserServiceImpl extends ServiceImpl<MobeUserMapper, MobeUser> implements MobeUserService {

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