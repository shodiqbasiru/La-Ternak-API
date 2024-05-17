package com.enigma.laternak.service;

import com.enigma.laternak.entity.ImageProduct;
import com.enigma.laternak.entity.Product;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageProductService {
    List<ImageProduct> create(List<MultipartFile> multipartFiles, Product product);
    Resource getById(String id);
    void deleteById(String id);
}
