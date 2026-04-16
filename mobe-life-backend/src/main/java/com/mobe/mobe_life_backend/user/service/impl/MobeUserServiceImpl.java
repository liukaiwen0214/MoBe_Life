/**
 * 核心职责：实现当前登录用户资料读取与更新逻辑。
 * 所属业务模块：用户中心 / 业务服务实现。
 * 重要依赖关系或外部约束：依赖 `UserContext` 获取当前身份，依赖 `mobe_user` 表维护用户资料。
 */
package com.mobe.mobe_life_backend.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mobe.mobe_life_backend.auth.entity.VerificationCode;
import com.mobe.mobe_life_backend.auth.mapper.VerificationCodeMapper;
import com.mobe.mobe_life_backend.common.context.UserContext;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.common.exception.AuthErrorCode;
import com.mobe.mobe_life_backend.common.exception.CommonErrorCode;
import com.mobe.mobe_life_backend.common.utils.VerificationCodeUtils;
import com.mobe.mobe_life_backend.user.dto.UnbindEmailDTO;
import com.mobe.mobe_life_backend.user.dto.UpdateUserProfileDTO;
import com.mobe.mobe_life_backend.user.entity.MobeUser;
import com.mobe.mobe_life_backend.user.mapper.MobeUserMapper;
import com.mobe.mobe_life_backend.user.service.MobeUserService;
import com.mobe.mobe_life_backend.user.vo.CurrentUserVO;

import java.time.LocalDateTime;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现。
 *
 * <p>
 * 设计初衷是把“当前登录用户”视角下的资料操作封装起来，
 * 让控制层看到的是稳定 VO，而不是直接暴露数据库实体。
 * </p>
 *
 * <p>
 * 线程安全性：无共享可变状态，作为 Spring 单例 Bean 使用安全。
 * </p>
 */
@Service
public class MobeUserServiceImpl extends ServiceImpl<MobeUserMapper, MobeUser> implements MobeUserService {

  private final VerificationCodeMapper verificationCodeMapper;

  public MobeUserServiceImpl(VerificationCodeMapper verificationCodeMapper) {
    this.verificationCodeMapper = verificationCodeMapper;
  }

  private static final String BIZ_TYPE_UNBIND_EMAIL = "UNBIND_EMAIL";
  private static final Integer TARGET_TYPE_EMAIL = 2;

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
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeUser user = this.getById(userId);
    if (user == null || Integer.valueOf(1).equals(user.getIsDeleted())) {
      throw new BusinessException(AuthErrorCode.USER_NOT_FOUND);
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
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeUser user = this.getById(userId);
    if (user == null || Integer.valueOf(1).equals(user.getIsDeleted())) {
      throw new BusinessException(AuthErrorCode.USER_NOT_FOUND);
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

  @Override
  public void unbindEmail(UnbindEmailDTO unbindEmailDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException(AuthErrorCode.TOKEN_MISSING);
    }

    MobeUser currentUser = this.getById(userId);
    if (currentUser == null || Integer.valueOf(1).equals(currentUser.getIsDeleted())) {
      throw new BusinessException(AuthErrorCode.USER_NOT_FOUND);
    }

    boolean hasPhone = currentUser.getPhone() != null && !currentUser.getPhone().isBlank();
    if (!hasPhone) {
      throw new BusinessException(CommonErrorCode.PARAMS_VALIDATION_FAILED, "请先绑定手机号后再解绑邮箱");
    }

    String email = currentUser.getEmail();
    if (email == null || email.isBlank()) {
      throw new BusinessException(CommonErrorCode.PARAMS_VALIDATION_FAILED, "当前账号未绑定邮箱");
    }

    VerificationCode verificationCode = verificationCodeMapper.selectOne(
        new LambdaQueryWrapper<VerificationCode>()
            .eq(VerificationCode::getTarget, email)
            .eq(VerificationCode::getTargetType, TARGET_TYPE_EMAIL)
            .eq(VerificationCode::getBizType, BIZ_TYPE_UNBIND_EMAIL)
            .eq(VerificationCode::getStatus, 0)
            .eq(VerificationCode::getIsDeleted, 0)
            .orderByDesc(VerificationCode::getId)
            .last("limit 1"));

    if (verificationCode == null) {
      throw new BusinessException(AuthErrorCode.VERIFICATION_CODE_ERROR, "验证码不存在或已失效");
    }

    if (verificationCode.getExpireTime() == null || verificationCode.getExpireTime().isBefore(LocalDateTime.now())) {
      verificationCode.setStatus(2);
      verificationCode.setRemark("解绑邮箱验证码已过期");
      verificationCodeMapper.updateById(verificationCode);
      throw new BusinessException(AuthErrorCode.VERIFICATION_CODE_EXPIRED);
    }

    String inputCodeHash = VerificationCodeUtils.hashCode(email, BIZ_TYPE_UNBIND_EMAIL,
        unbindEmailDTO.getCode().trim());
    if (!inputCodeHash.equals(verificationCode.getCodeHash())) {
      verificationCode
          .setFailCount((verificationCode.getFailCount() == null ? 0 : verificationCode.getFailCount()) + 1);
      verificationCodeMapper.updateById(verificationCode);
      throw new BusinessException(AuthErrorCode.VERIFICATION_CODE_ERROR);
    }

    this.update(
        null,
        new LambdaUpdateWrapper<MobeUser>()
            .eq(MobeUser::getId, userId)
            .eq(MobeUser::getIsDeleted, 0)
            .set(MobeUser::getEmail, null));

    verificationCode.setStatus(1);
    verificationCode.setUsedTime(LocalDateTime.now());
    verificationCode.setRemark("解绑邮箱成功");
    verificationCodeMapper.updateById(verificationCode);
  }
}
