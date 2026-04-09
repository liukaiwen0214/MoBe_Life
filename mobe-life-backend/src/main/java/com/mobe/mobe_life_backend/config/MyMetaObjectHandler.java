/**
 * 核心职责：统一自动填充数据库实体的创建时间和更新时间。
 * 所属业务模块：系统基础设施 / ORM 辅助配置。
 * 重要依赖关系或外部约束：依赖 MyBatis Plus 的自动填充机制；实体字段名必须与这里填充的属性名一致。
 */
package com.mobe.mobe_life_backend.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 元数据填充器。
 *
 * <p>设计初衷是消除业务层手工维护审计时间字段的重复代码，
 * 也避免不同模块各自填充时间导致口径不一致。</p>
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

  /**
   * 插入时自动填充。
   *
   * @param metaObject 当前实体元数据，不允许为 null。
   * @implNote 在插入阶段同时写入 `createTime` 和 `updateTime`，确保新记录的两个时间字段一致。
   */
  @Override
  public void insertFill(MetaObject metaObject) {
    LocalDateTime now = LocalDateTime.now();
    this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
    this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
  }

  /**
   * 更新时自动填充。
   *
   * @param metaObject 当前实体元数据，不允许为 null。
   * @implNote 这里只更新 `updateTime`，避免覆盖历史创建时间。
   */
  @Override
  public void updateFill(MetaObject metaObject) {
    this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
  }
}
