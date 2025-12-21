package com.school.homework.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String saveFile(MultipartFile file);
    void deleteFile(String fileName);
}

