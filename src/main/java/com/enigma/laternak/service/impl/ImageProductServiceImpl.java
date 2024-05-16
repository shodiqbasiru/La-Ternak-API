package com.enigma.laternak.service.impl;

import com.enigma.laternak.entity.ImageProduct;
import com.enigma.laternak.entity.Product;
import com.enigma.laternak.repository.ImageProductRepository;
import com.enigma.laternak.service.ImageProductService;
import com.enigma.laternak.util.ResponseMessage;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.enigma.laternak.constant.Message.*;

@Service
public class ImageProductServiceImpl implements ImageProductService {
    private final Path directoryPath;
    private final ImageProductRepository imageProductRepository;

    @Autowired
    public ImageProductServiceImpl(@Value("${la_ternak.multipart.path-location-image-product}") String directoryPath, ImageProductRepository imageProductRepository) {
        this.directoryPath = Paths.get(directoryPath);
        this.imageProductRepository = imageProductRepository;
    }

    @PostConstruct
    public void iniDirectory() {
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectory(directoryPath);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ImageProduct> create(List<MultipartFile> multipartFiles, Product product) {
        List<ImageProduct> images = multipartFiles.stream().map(file -> {
            try {
                if (!List.of("image/jpeg", "image/png", "image/jpg").contains(file.getContentType())) {
                    throw new ConstraintViolationException(ERROR_IMAGE_CONTENT_TYPE.getMessage(), null);
                }

                String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path path = directoryPath.resolve(filename);
                Files.copy(file.getInputStream(), path);

                ImageProduct imageProduct = ImageProduct.builder()
                        .name(filename)
                        .path(path.toString())
                        .size(file.getSize())
                        .contentType(file.getContentType())
                        .product(product)
                        .build();
                return imageProductRepository.saveAndFlush(imageProduct);
            } catch (IOException e) {
                throw ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERNAL_SERVER_ERROR);
            }
        }).toList();
        return images;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resource getById(String id) {
        try {
            ImageProduct image = imageProductRepository.findById(id).orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, ERROR_IMAGE_NOT_FOUND));
            Path path = Paths.get(image.getPath());
            if (!Files.exists(path)) throw ResponseMessage.error(HttpStatus.NOT_FOUND, ERROR_IMAGE_NOT_FOUND);
            return new UrlResource(path.toUri());
        } catch (IOException e) {
            throw ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            ImageProduct image = imageProductRepository.findById(id).orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, ERROR_IMAGE_NOT_FOUND));
            Path path = Paths.get(image.getPath());
            if (!Files.exists(path)) throw ResponseMessage.error(HttpStatus.NOT_FOUND, ERROR_IMAGE_NOT_FOUND);
            Files.delete(path);
            imageProductRepository.delete(image);
        } catch (IOException e) {
            throw ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR, ERROR_INTERNAL_SERVER_ERROR);
        }
    }
}
