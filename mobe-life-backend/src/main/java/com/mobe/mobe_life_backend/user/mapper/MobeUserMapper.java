/**
 * 核心职责：提供用户表的持久化入口。
 * 所属业务模块：用户中心 / 数据访问层。
 * 重要依赖关系或外部约束：依赖 MyBatis Plus；认证和用户模块都通过它访问 `mobe_user` 表。
 */
package com.mobe.mobe_life_backend.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mobe.mobe_life_backend.user.entity.MobeUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper。
 *
 * <p>设计初衷是让用户数据访问有统一边界，便于未来扩展更复杂的用户查询。</p>
 */
@Mapper
public interface MobeUserMapper extends BaseMapper<MobeUser> {
}
