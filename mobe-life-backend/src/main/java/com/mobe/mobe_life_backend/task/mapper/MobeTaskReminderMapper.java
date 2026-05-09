/**
 * 声明待办中心的 Mapper，集中数据库访问入口。
 * 模块：待办中心 / 数据访问层。
 * 约束：通常由 MyBatis 或 MyBatis-Plus 生成代理实现，方法签名需与 XML 或框架约定对应。
 */
package com.mobe.mobe_life_backend.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mobe.mobe_life_backend.task.entity.MobeTaskReminder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MobeTaskReminderMapper extends BaseMapper<MobeTaskReminder> {
}
