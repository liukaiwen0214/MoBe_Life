/**
 * 核心职责：向客户端返回上传成功后的文件信息。
 * 所属业务模块：文件服务 / 上传响应。
 * 重要依赖关系或外部约束：字段结构需兼容前端头像上传后的预览与提交逻辑。
 */
package com.mobe.mobe_life_backend.file.vo;

import lombok.Data;

/**
 * 文件上传结果。
 */
@Data
public class UploadFileVO {

  /** 服务端生成的文件名。 */
  private String fileName;

  /** 对外可访问的文件 URL。 */
  private String url;
}
