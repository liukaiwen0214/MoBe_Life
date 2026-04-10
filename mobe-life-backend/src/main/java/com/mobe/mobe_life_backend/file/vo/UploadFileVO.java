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
