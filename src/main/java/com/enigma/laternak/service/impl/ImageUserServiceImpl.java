package com.enigma.laternak.service.impl;
import com.enigma.laternak.constant.Message;
import com.enigma.laternak.entity.ImageUser;
import com.enigma.laternak.repository.ImageUserRepository;
import com.enigma.laternak.service.ImageUserService;
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

@Service
public class ImageUserServiceImpl implements ImageUserService {
    private final Path directoryPath;
    private final ImageUserRepository imageUserRepository;

    @Autowired
    public ImageUserServiceImpl(@Value("${la_ternak.multipart.path-location-image-user}") String directoryPath, ImageUserRepository imageUserRepository) {
        this.directoryPath = Paths.get(directoryPath);
        this.imageUserRepository = imageUserRepository;
    }

    @PostConstruct
    public void iniDirectory(){
        if (!Files.exists(directoryPath)){
            try{
                Files.createDirectory(directoryPath);
            }catch (IOException e){
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImageUser create(MultipartFile multipartFile) {
        try{
            if(!List.of("image/jpeg", "image/png", "image/jpg").contains(multipartFile.getContentType())){
                throw new ConstraintViolationException(Message.ERROR_IMAGE_CONTENT_TYPE.getMessage(), null);
            }
            String uniqueFileName=System.currentTimeMillis()+"_"+multipartFile.getOriginalFilename();
            Path filePath=directoryPath.resolve(uniqueFileName);
            Files.copy(multipartFile.getInputStream(), filePath);
            ImageUser image=ImageUser.builder()
                    .name(multipartFile.getOriginalFilename())
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .path(filePath.toString())
                    .build();
            imageUserRepository.saveAndFlush(image);
            return image;
        }catch (IOException e){
            throw ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR, Message.ERROR_INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resource getById(String id) {
        try{
            ImageUser image=imageUserRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "file not found"));
            Path path=Paths.get(image.getPath());
            if (!Files.exists(path)) throw ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_IMAGE_NOT_FOUND);
            return new UrlResource(path.toUri());
        }catch (IOException e){
            throw ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR, Message.ERROR_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            ImageUser image = imageUserRepository.findById(id).orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_IMAGE_NOT_FOUND));
            Path path = Paths.get(image.getPath());
            if (!Files.exists(path)) throw ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_IMAGE_NOT_FOUND);
            Files.delete(path);
            imageUserRepository.delete(image);
        } catch (IOException e) {
            throw ResponseMessage.error(HttpStatus.INTERNAL_SERVER_ERROR, Message.ERROR_INTERNAL_SERVER_ERROR);
        }
    }
}
