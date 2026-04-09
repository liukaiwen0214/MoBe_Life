/**
 * 核心职责：暴露文件上传接口。
 * 所属业务模块：文件服务 / 表现层。
 * 重要依赖关系或外部约束：依赖 `FileService` 完成存储；当前只开放头像上传，默认要求文件为图片。
 */
package com.mobe.mobe_life_backend.file.controller;

import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.file.service.FileService;
import com.mobe.mobe_life_backend.file.vo.UploadFileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件控制器。
 *
 * <p>设计初衷是把文件上传从用户资料更新逻辑中拆出来，避免一个接口同时处理二进制流和资料字段造成维护复杂度上升。</p>
 */
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

  /** 文件服务。 */
  private final FileService fileService;

  /**
   * 上传头像图片。
   *
   * @param file 上传文件，不允许为 null 或空文件。
   * @return 上传结果，不返回 null；包含服务端生成的文件名和访问地址。
   * @throws RuntimeException 当文件为空、后缀非法或磁盘写入失败时抛出。
   * @implNote 该接口会写入本地磁盘。
   */
  @PostMapping("/upload/avatar")
  public Result<UploadFileVO> uploadAvatar(@RequestPart("file") MultipartFile file) {
    return Result.success(fileService.uploadAvatar(file));
  }
}
