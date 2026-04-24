/**
 * 核心职责：对外暴露文件中心相关接口，负责接收请求并调用对应业务能力。
 * 所属业务模块：文件中心 / 控制层。
 * 重要依赖关系或外部约束：依赖 Spring MVC 与服务层；通常不承载复杂业务逻辑。
 */
package com.mobe.mobe_life_backend.file.controller;

import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.file.service.FileService;
import com.mobe.mobe_life_backend.file.vo.UploadFileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件模块接口", description = "提供用户头像等文件上传能力")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;

  /**
   * 上传头像文件。
   *
   * @return 返回上传后的文件信息。
   */
  @Operation(summary = "上传头像文件", description = "上传用户头像图片并返回文件访问地址")
  @PostMapping("/upload/avatar")
  public Result<UploadFileVO> uploadAvatar(
      @Parameter(description = "头像图片文件，支持前端通过 multipart/form-data 上传", required = true) @RequestPart("file") MultipartFile file) {
    return Result.success(fileService.uploadAvatar(file));
  }
}
