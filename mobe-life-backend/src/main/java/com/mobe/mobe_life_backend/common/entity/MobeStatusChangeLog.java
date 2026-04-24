/**
 * 核心职责：定义公共基础设施的数据实体，用于映射数据库记录或领域状态。
 * 所属业务模块：公共基础设施 / 实体模型。
 * 重要依赖关系或外部约束：字段通常需要与数据库表结构、MyBatis-Plus 映射约定保持一致。
 */
package com.mobe.mobe_life_backend.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@TableName("mobe_status_change_log")
public class MobeStatusChangeLog {

  @TableId(type = IdType.AUTO)
  private Long id;

  private Long userId;

  private String bizType;

  private Long bizId;

  private Long statusTemplateId;

  private Long fromStatusId;

  private String fromStatusCode;

  private String fromStatusName;

  private Long toStatusId;

  private String toStatusCode;

  private String toStatusName;

  private String changeType;

  private String changeRemark;

  private Long operatorId;

  private LocalDateTime createTime;

  private LocalDateTime updateTime;

  private Integer isDeleted;
}