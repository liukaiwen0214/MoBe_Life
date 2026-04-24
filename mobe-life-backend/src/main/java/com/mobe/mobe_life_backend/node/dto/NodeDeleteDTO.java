/**
 * 核心职责：封装节点中心的请求参数模型，承载控制层到服务层的输入数据。
 * 所属业务模块：节点中心 / DTO。
 * 重要依赖关系或外部约束：通常与参数校验注解配合使用，字段命名需要与接口语义保持一致。
 */
package com.mobe.mobe_life_backend.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除节点请求")
public class NodeDeleteDTO {

  @Schema(description = "删除模式：COMPLETE_TASKS/DELETE_TASKS")
  private String deleteMode;
}