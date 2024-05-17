package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.service.ImageProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Tag(name = "Image Product", description = "API for Image Product")
public class ImageProductController {
    private final ImageProductService imageService;

    @Operation(
            summary = "Download image product",
            description = "Download image product"
    )
    @GetMapping(path = ApiRoute.IMAGE_PRODUCT_API+"/{imageId}")
    public ResponseEntity<?> downloadImage(@PathVariable String imageId){
        Resource resource=imageService.getById(imageId);
        String headerValue = String.format("attachment; filename=%s", resource.getFilename());
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(resource);
    }
}
