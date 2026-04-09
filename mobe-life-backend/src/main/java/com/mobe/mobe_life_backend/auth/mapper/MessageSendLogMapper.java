/**
 * 核心职责：提供消息发送日志表的持久化入口。
 * 所属业务模块：认证中心 / 数据访问层。
 * 重要依赖关系或外部约束：依赖 MyBatis Plus 生成基础 CRUD；复杂查询需注意与发送状态语义保持一致。
 */
package com.mobe.mobe_life_backend.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mobe.mobe_life_backend.auth.entity.MessageSendLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息发送日志 Mapper。
 *
 * <p>设计初衷是把认证模块对发送日志的读写收敛到统一入口，避免服务层直接拼装 SQL，
 * 也便于后续针对发送失败日志增加专用查询方法。</p>
 *
 * <p>线程安全性：Mapper 由 MyBatis/Spring 代理管理，可安全地在多线程请求中复用。</p>
 */
@Mapper
public interface MessageSendLogMapper extends BaseMapper<MessageSendLog> {
}
