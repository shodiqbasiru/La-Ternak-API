package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.service.ImageStoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Image Store", description = "API for Image Store")
public class ImageStoreController {
    private final ImageStoreService imageStoreService;

    @Operation(
            summary = "Download image Store",
            description = "Download image Store"
    )
    @GetMapping(path = ApiRoute.IMAGE_STORE_API + "/{imageId}")
    public ResponseEntity<?> download(@PathVariable String imageId) {
        Resource resource = imageStoreService.getById(imageId);
        String headerValue = String.format("attachment; filename=%s", resource.getFilename());
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION,headerValue)
                .body(resource);
    }

}
