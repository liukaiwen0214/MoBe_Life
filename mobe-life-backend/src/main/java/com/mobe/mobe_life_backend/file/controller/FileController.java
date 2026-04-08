package com.mobe.mobe_life_backend.file.controller;

import com.mobe.mobe_life_backend.common.response.Result;
import com.mobe.mobe_life_backend.file.service.FileService;
import com.mobe.mobe_life_backend.file.vo.UploadFileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

  private final FileService fileService;

  @PostMapping("/upload/avatar")
  public Result<UploadFileVO> uploadAvatar(@RequestPart("file") MultipartFile file) {
    return Result.success(fileService.uploadAvatar(file));
  }
}