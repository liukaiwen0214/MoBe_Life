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