/**
 * 封装节点中心的请求入参，承接控制层到服务层的字段传递。
 * 模块：节点中心 / DTO。
 * 约束：通常与参数校验注解配合使用，字段命名需要与接口语义保持一致。
 */
package com.mobe.mobe_life_backend.node.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "编辑节点请求")
public class NodeUpdateDTO {

  @Schema(description = "节点名称", requiredMode = Schema.RequiredMode.REQUIRED)
  private String title;

  @Schema(description = "节点说明")
  private String content;

  @Schema(description = "备注")
  private String remark;
}