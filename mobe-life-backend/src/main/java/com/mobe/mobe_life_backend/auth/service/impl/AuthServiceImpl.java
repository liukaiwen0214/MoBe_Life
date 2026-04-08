package com.mobe.mobe_life_backend.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mobe.mobe_life_backend.auth.dto.BindPhoneDTO;
import com.mobe.mobe_life_backend.auth.dto.WxMiniLoginDTO;
import com.mobe.mobe_life_backend.auth.service.AuthService;
import com.mobe.mobe_life_backend.auth.service.WechatMiniAppService;
import com.mobe.mobe_life_backend.auth.vo.LoginUserVO;
import com.mobe.mobe_life_backend.auth.vo.TokenVO;
import com.mobe.mobe_life_backend.auth.vo.WxCode2SessionVO;
import com.mobe.mobe_life_backend.auth.vo.WxPhoneNumberVO;
import com.mobe.mobe_life_backend.common.context.UserContext;
import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.common.utils.JwtUtils;
import com.mobe.mobe_life_backend.user.entity.MobeUser;
import com.mobe.mobe_life_backend.user.service.MobeUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final MobeUserService mobeUserService;
  private final WechatMiniAppService wechatMiniAppService;

  @Override
  public LoginUserVO wxMiniLogin(WxMiniLoginDTO wxMiniLoginDTO) {
    WxCode2SessionVO wxSession = wechatMiniAppService.code2Session(wxMiniLoginDTO.getCode());

    String openid = wxSession.getOpenid();
    String unionid = wxSession.getUnionid();

    MobeUser user = mobeUserService.getOne(
        new LambdaQueryWrapper<MobeUser>().eq(MobeUser::getOpenid, openid));

    if (user == null) {
      user = new MobeUser();
      user.setOpenid(openid);
      user.setUnionid(unionid);
      user.setNickname("微信用户");
      user.setStatus(0);
      user.setGender(0);
      user.setIsDeleted(0);
      mobeUserService.save(user);
    } else if (unionid != null && !unionid.isBlank() && user.getUnionid() == null) {
      user.setUnionid(unionid);
      mobeUserService.updateById(user);
    }

    String token = JwtUtils.createToken(user.getId());

    LoginUserVO loginUserVO = new LoginUserVO();
    loginUserVO.setUserId(user.getId());
    loginUserVO.setNickname(user.getNickname());
    loginUserVO.setAvatar(user.getAvatar());
    loginUserVO.setToken(token);

    return loginUserVO;
  }

  @Override
  public TokenVO refreshToken(String authorization) {
    if (authorization == null || authorization.isBlank()) {
      throw new BusinessException("未登录或token为空");
    }

    String token = authorization;
    if (token.startsWith("Bearer ")) {
      token = token.substring(7);
    }

    if (!JwtUtils.isValid(token)) {
      throw new BusinessException("token无效或已过期");
    }

    Long userId = JwtUtils.getUserId(token);
    String newToken = JwtUtils.createToken(userId);

    TokenVO tokenVO = new TokenVO();
    tokenVO.setToken(newToken);
    return tokenVO;
  }

  @Override
  public void logout() {
    // 第一版JWT无状态，服务端暂不做额外处理
  }

  @Override
  public void bindPhone(BindPhoneDTO bindPhoneDTO) {
    Long userId = UserContext.getCurrentUserId();
    if (userId == null) {
      throw new BusinessException("当前用户未登录");
    }

    WxPhoneNumberVO phoneInfo = wechatMiniAppService.getPhoneNumber(bindPhoneDTO.getCode());
    String phone = phoneInfo.getPurePhoneNumber();
    if (phone == null || phone.isBlank()) {
      throw new BusinessException("获取手机号失败");
    }

    MobeUser exist = mobeUserService.getOne(
        new LambdaQueryWrapper<MobeUser>().eq(MobeUser::getPhone, phone));
    if (exist != null && !exist.getId().equals(userId)) {
      throw new BusinessException("该手机号已绑定其他账号");
    }

    MobeUser currentUser = mobeUserService.getById(userId);
    if (currentUser == null) {
      throw new BusinessException("用户不存在");
    }

    currentUser.setPhone(phone);
    mobeUserService.updateById(currentUser);
  }
}