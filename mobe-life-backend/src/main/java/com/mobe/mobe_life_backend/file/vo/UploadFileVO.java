/**
 * 核心职责：封装文件中心返回给前端或上层调用方的展示模型。
 * 所属业务模块：文件中心 / VO。
 * 重要依赖关系或外部约束：字段设计更偏向展示与接口契约，不一定与数据库结构一一对应。
 */
package com.mobe.mobe_life_backend.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "文件上传响应对象，用于返回上传后的文件名称与访问地址")
@Data
public class UploadFileVO {

  @Schema(description = "服务端生成的文件名", example = "avatar_20260410100000.png")
  private String fileName;

  @Schema(description = "文件访问地址", example = "https://cdn.mobe.com/upload/avatar_20260410100000.png")
  private String url;
}
