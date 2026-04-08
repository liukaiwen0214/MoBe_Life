package com.mobe.mobe_life_backend.file.service;

import com.mobe.mobe_life_backend.file.vo.UploadFileVO;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

  UploadFileVO uploadAvatar(MultipartFile file);
}