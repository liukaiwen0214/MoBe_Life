/**
 * 封装待办中心的请求入参，承接控制层到服务层的字段传递。
 * 模块：待办中心 / DTO。
 * 约束：通常与参数校验注解配合使用，字段命名需要与接口语义保持一致。
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