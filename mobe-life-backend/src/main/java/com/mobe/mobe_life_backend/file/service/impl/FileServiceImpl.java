package com.mobe.mobe_life_backend.file.service.impl;

import com.mobe.mobe_life_backend.common.exception.BusinessException;
import com.mobe.mobe_life_backend.file.config.FileUploadProperties;
import com.mobe.mobe_life_backend.file.service.FileService;
import com.mobe.mobe_life_backend.file.vo.UploadFileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  private final FileUploadProperties fileUploadProperties;

  @Override
  public UploadFileVO uploadAvatar(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw new BusinessException("上传文件不能为空");
    }

    String originalFilename = file.getOriginalFilename();
    String suffix = getFileSuffix(originalFilename);

    if (!isImageSuffix(suffix)) {
      throw new BusinessException("仅支持上传 jpg、jpeg、png、webp 图片");
    }

    String fileName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;
    String avatarDirPath = fileUploadProperties.getPath() + File.separator + "avatar";
    File avatarDir = new File(avatarDirPath);

    if (!avatarDir.exists() && !avatarDir.mkdirs()) {
      throw new BusinessException("创建上传目录失败");
    }

    File destFile = new File(avatarDir, fileName);
    try {
      file.transferTo(destFile);
    } catch (IOException e) {
      throw new BusinessException("文件上传失败");
    }

    UploadFileVO uploadFileVO = new UploadFileVO();
    uploadFileVO.setFileName(fileName);
    uploadFileVO.setUrl(fileUploadProperties.getAccessUrlPrefix() + "/avatar/" + fileName);
    return uploadFileVO;
  }

  private String getFileSuffix(String fileName) {
    if (fileName == null || !fileName.contains(".")) {
      throw new BusinessException("文件后缀不合法");
    }
    return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
  }

  private boolean isImageSuffix(String suffix) {
    return "jpg".equals(suffix)
        || "jpeg".equals(suffix)
        || "png".equals(suffix)
        || "webp".equals(suffix);
  }
}