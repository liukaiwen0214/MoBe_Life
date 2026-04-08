package com.mobe.mobe_life_backend.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mobe.mobe_life_backend.user.dto.UpdateUserProfileDTO;
import com.mobe.mobe_life_backend.user.entity.MobeUser;
import com.mobe.mobe_life_backend.user.vo.CurrentUserVO;

public interface MobeUserService extends IService<MobeUser> {
  CurrentUserVO getCurrentUser();

  void updateCurrentUserProfile(UpdateUserProfileDTO updateUserProfileDTO);
}