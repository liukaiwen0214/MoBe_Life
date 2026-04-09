/**
 * 核心职责：实现头像文件上传到本地磁盘的逻辑。
 * 所属业务模块：文件服务 / 业务服务实现。
 * 重要依赖关系或外部约束：依赖本地文件系统可写；当前只允许图片后缀，且不会做内容嗅探。
 */
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

/**
 * 本地文件上传实现。
 *
 * <p>设计初衷是在项目早期用最低复杂度支持头像上传能力，
 * 因此选择本地磁盘落盘而不是直接接入对象存储。</p>
 *
 * <p>线程安全性：无共享可变状态，线程安全；真正的并发冲突主要由磁盘 IO 和文件名唯一性保障。</p>
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

  /** 文件上传配置。 */
  private final FileUploadProperties fileUploadProperties;

  /**
   * 上传头像。
   *
   * @param file 文件对象，不允许为 null 或空文件。
   * @return 上传结果，不返回 null；成功时包含服务端生成文件名和可访问 URL。
   * @throws BusinessException 当文件为空、后缀非法、目录创建失败或文件写入失败时抛出。
   * @implNote 该方法会写入本地磁盘。
   */
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

    // 使用随机文件名而不是原文件名，避免同名覆盖，也减少用户上传恶意文件名带来的路径污染风险。
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

  /**
   * 提取文件后缀。
   *
   * @param fileName 原始文件名，允许为 null。
   * @return 小写后缀，不包含点号。
   * @throws BusinessException 当文件名为空、缺少后缀时抛出。
   */
  private String getFileSuffix(String fileName) {
    if (fileName == null || !fileName.contains(".")) {
      throw new BusinessException("文件后缀不合法");
    }
    return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(Locale.ROOT);
  }

  /**
   * 判断后缀是否为允许的图片类型。
   *
   * @param suffix 文件后缀，允许为 null。
   * @return 允许返回 `true`，否则返回 `false`。
   * @implNote 当前只校验后缀，不校验 MIME 或文件内容，因此生产环境若安全要求更高应增加内容检测。
   */
  private boolean isImageSuffix(String suffix) {
    return "jpg".equals(suffix)
        || "jpeg".equals(suffix)
        || "png".equals(suffix)
        || "webp".equals(suffix);
  }
}
