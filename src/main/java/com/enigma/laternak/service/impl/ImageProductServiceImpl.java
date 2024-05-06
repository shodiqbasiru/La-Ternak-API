package com.enigma.laternak.service.impl;
import com.enigma.laternak.entity.ImageProduct;
import com.enigma.laternak.entity.Product;
import com.enigma.laternak.repository.ImageProductRepository;
import com.enigma.laternak.repository.ProductRepository;
import com.enigma.laternak.service.ImageProductService;
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
public class ImageProductServiceImpl implements ImageProductService {
    private final Path directoryPath;
    private final ImageProductRepository imageProductRepository;
    private final ProductRepository productRepository;

    @Autowired
    public ImageProductServiceImpl(@Value("${la_ternak.multipart.path-location}") String directoryPath, ImageProductRepository imageProductRepository, ProductRepository productRepository) {
        this.directoryPath = Paths.get(directoryPath);
        this.imageProductRepository = imageProductRepository;
        this.productRepository=productRepository;
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
    public ImageProduct create(MultipartFile multipartFile, String idProduct) {
        try{
            if(!List.of("image/jpeg", "image/png", "image/jpg").contains(multipartFile.getContentType())){
                throw new ConstraintViolationException("Type data is false", null);
            }
            Product product=productRepository.findById(idProduct).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is not found"));
            String uniqueFileName=System.currentTimeMillis()+"_"+multipartFile.getOriginalFilename();
            Path filePath=directoryPath.resolve(uniqueFileName);
            Files.copy(multipartFile.getInputStream(), filePath);
            ImageProduct image=ImageProduct.builder()
                    .name(multipartFile.getOriginalFilename())
                    .contentType(multipartFile.getContentType())
                    .size(multipartFile.getSize())
                    .path(filePath.toString())
                    .product(product)
                    .build();
            imageProductRepository.saveAndFlush(image);
            return image;
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resource getById(String id) {
        try{
            ImageProduct image=imageProductRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "file not found"));
            Path path=Paths.get(image.getPath());
            if (!Files.exists(path)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "file not found");
            return new UrlResource(path.toUri());
        }catch (IOException e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            ImageProduct image = imageProductRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "file not found"));
            Path path = Paths.get(image.getPath());
            if (!Files.exists(path)) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "file not found");
            Files.delete(path);
            imageProductRepository.delete(image);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
