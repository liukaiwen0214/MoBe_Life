/**
 * 核心职责：抽象文件上传业务能力。
 * 所属业务模块：文件服务 / 业务服务接口。
 * 重要依赖关系或外部约束：实现类可能基于本地磁盘、对象存储或 CDN；当前接口只关注“头像上传”这一业务语义。
 */
package com.mobe.mobe_life_backend.file.service;

import com.mobe.mobe_life_backend.file.vo.UploadFileVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口。
 */
public interface FileService {

  /**
   * 上传用户头像。
   *
   * @param file 文件对象，不允许为 null 或空文件。
   * @return 上传结果，不返回 null。
   * @throws RuntimeException 当文件校验失败或存储失败时抛出。
   */
  UploadFileVO uploadAvatar(MultipartFile file);
}
