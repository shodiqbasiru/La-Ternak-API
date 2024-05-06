package com.enigma.laternak.service;

import com.enigma.laternak.entity.ImageProduct;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ImageProductService {
    ImageProduct create(MultipartFile multipartFile, String IdProduct);
    Resource getById(String id);
    void deleteById(String id);
}
