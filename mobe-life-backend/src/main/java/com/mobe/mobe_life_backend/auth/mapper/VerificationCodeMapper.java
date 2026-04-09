/**
 * 核心职责：提供验证码表的持久化入口。
 * 所属业务模块：认证中心 / 数据访问层。
 * 重要依赖关系或外部约束：依赖 MyBatis Plus；查询“最新有效验证码”时默认按主键倒序读取，前提是主键单调递增。
 */
package com.mobe.mobe_life_backend.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mobe.mobe_life_backend.auth.entity.VerificationCode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 验证码 Mapper。
 *
 * <p>设计初衷是让验证码生命周期操作集中在一个数据访问边界内，方便后续增加失效清理或统计查询。</p>
 *
 * <p>线程安全性：由框架代理管理，可作为单例安全复用。</p>
 */
@Mapper
public interface VerificationCodeMapper extends BaseMapper<VerificationCode> {
}
