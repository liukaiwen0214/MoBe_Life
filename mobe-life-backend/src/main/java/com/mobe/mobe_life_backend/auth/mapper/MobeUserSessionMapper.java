/**
 * 核心职责：提供用户登录会话表的持久化入口。
 * 所属业务模块：认证中心 / 登录会话。
 * 重要依赖关系或外部约束：依赖 MyBatis Plus；访问令牌 jti 是会话校验的核心查询键。
 */
package com.mobe.mobe_life_backend.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mobe.mobe_life_backend.auth.entity.MobeUserSession;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MobeUserSessionMapper extends BaseMapper<MobeUserSession> {
}
