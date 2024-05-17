package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.Message;
import com.enigma.laternak.entity.ImageStore;
import com.enigma.laternak.repository.ImageStoreRepository;
import com.enigma.laternak.service.ImageStoreService;
import com.enigma.laternak.util.ResponseMessage;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ImageStoreServiceImpl implements ImageStoreService {

    private final ImageStoreRepository imageStoreRepository;
    private final Path directoryPath;

    public ImageStoreServiceImpl(ImageStoreRepository imageStoreRepository, @Value("${la_ternak.multipart.path-location-image-store}") String directoryPath) {
        this.imageStoreRepository = imageStoreRepository;
        this.directoryPath = Paths.get(directoryPath);
    }

    @PostConstruct
    public void initDirectory() {
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectory(directoryPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public ImageStore create(MultipartFile multipartFile) {
        try {
            if (!List.of("image/jpeg", "image/jpg", "image/svg", "image/png").contains(multipartFile.getContentType()))
                throw new ConstraintViolationException(Message.ERROR_IMAGE_CONTENT_TYPE.getMessage(), null);
            String originalFilename = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
            Path filePath = directoryPath.resolve(originalFilename);
            Files.copy(multipartFile.getInputStream(), filePath);

            ImageStore image = ImageStore.builder()
                    .name(originalFilename)
                    .path(filePath.toString())
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .build();
            return imageStoreRepository.saveAndFlush(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Resource getById(String id) {
        try {
            ImageStore image = imageStoreRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            Path filePath = Paths.get(image.getPath());
            if (!Files.exists(filePath))
                throw ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_IMAGE_NOT_FOUND);
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            throw ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR, Message.ERROR_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            ImageStore image = imageStoreRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
            Path filePath = Paths.get(image.getPath());
            if (!Files.exists(filePath))
                throw ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_IMAGE_NOT_FOUND);
            Files.delete(filePath);
            imageStoreRepository.delete(image);
        } catch (IOException e) {
            throw ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR, Message.ERROR_INTERNAL_SERVER_ERROR);
        }
    }
}
