package com.enigma.laternak.service;

import com.enigma.laternak.entity.ImageStore;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStoreService {
    ImageStore create(MultipartFile multipartFile);
    Resource getById(String id);
    void deleteById(String id);
}
