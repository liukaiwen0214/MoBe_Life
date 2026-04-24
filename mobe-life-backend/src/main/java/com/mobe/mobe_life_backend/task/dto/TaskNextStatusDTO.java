/**
 * 核心职责：封装待办中心的请求参数模型，承载控制层到服务层的输入数据。
 * 所属业务模块：待办中心 / DTO。
 * 重要依赖关系或外部约束：通常与参数校验注解配合使用，字段命名需要与接口语义保持一致。
 */
package com.mobe.mobe_life_backend.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "待办进入下一个状态请求")
public class TaskNextStatusDTO {

  @Schema(description = "变更说明")
  private String changeRemark;
}