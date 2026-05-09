/**
 * 封装项目中心对外输出的视图字段。
 * 模块：项目中心 / VO。
 * 约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
package com.mobe.mobe_life_backend.project.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Schema(description = "项目详情-日志项")
public class ProjectLogItemVO {

  @Schema(description = "日志ID")
  private Long id;

  @Schema(description = "日志类型")
  private String logType;

  @Schema(description = "日志描述")
  private String logText;

  @Schema(description = "创建时间")
  private LocalDateTime createTime;
}