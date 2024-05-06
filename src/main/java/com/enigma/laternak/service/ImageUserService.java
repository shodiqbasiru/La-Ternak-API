package com.enigma.laternak.service;

import com.enigma.laternak.entity.ImageUser;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;

public interface ImageUserService {
    ImageUser create(MultipartFile multipartFile);
    Resource getById(String id);
    void deleteById(String id);
}
