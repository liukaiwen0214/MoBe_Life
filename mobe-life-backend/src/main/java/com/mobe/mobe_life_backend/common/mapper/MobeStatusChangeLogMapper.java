/**
 * 核心职责：声明公共基础设施的数据访问接口，负责组织数据库查询与持久化入口。
 * 所属业务模块：公共基础设施 / 数据访问层。
 * 重要依赖关系或外部约束：通常由 MyBatis 或 MyBatis-Plus 生成代理实现，方法签名需与 XML 或框架约定对应。
 */
package com.mobe.mobe_life_backend.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mobe.mobe_life_backend.common.entity.MobeStatusChangeLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MobeStatusChangeLogMapper extends BaseMapper<MobeStatusChangeLog> {
}