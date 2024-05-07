package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.ProductRequest;
import com.enigma.laternak.dto.request.SearchProductRequest;
import com.enigma.laternak.dto.request.UpdateProductRequest;
import com.enigma.laternak.dto.response.CommonResponse;
import com.enigma.laternak.dto.response.PagingResponse;
import com.enigma.laternak.dto.response.ProductResponse;
import com.enigma.laternak.service.ProductService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiRoute.PRODUCT_API)
@Tag(name = "Product", description = "Api for product")
public class ProductController {
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    @Operation(
            summary = "Create Product",
            description = "Create a new product"
    )
    @SecurityRequirement(name = "Authorization")
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<ProductResponse>> create(
            @RequestPart(name = "product") String jsonProduct,
            @RequestPart(name = "image", required = false) List<MultipartFile> images
    ) {
        CommonResponse.CommonResponseBuilder<ProductResponse> builder = CommonResponse.builder();
        try {
            ProductRequest request = objectMapper.readValue(jsonProduct, new TypeReference<>() {
            });
            request.setImages(images);

            ProductResponse response = productService.create(request);
            builder.statusCode(HttpStatus.CREATED.value());
            builder.message("Created Data Successfully");
            builder.data(response);
            return ResponseEntity.status(HttpStatus.CREATED).body(builder.build());
        } catch (Exception e) {
            builder.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            builder.message(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(builder.build());
        }
    }


    @Operation(
            summary = "Update Product",
            description = "Update product"
    )
    @SecurityRequirement(name = "Authorization")
    @PutMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<String>> update(
            @RequestPart(name = "product") String jsonProduct,
            @RequestPart(name = "image", required = false) List<MultipartFile> images
    ) {
        CommonResponse.CommonResponseBuilder<String> builder = CommonResponse.builder();
        try {
            UpdateProductRequest request = objectMapper.readValue(jsonProduct, new TypeReference<>() {
            });
            request.setImages(images);

            productService.update(request);
            builder.statusCode(HttpStatus.OK.value());
            builder.message("Update Data Successfully");
            return ResponseEntity.status(HttpStatus.OK).body(builder.build());
        } catch (Exception e) {
            builder.statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            builder.message(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(builder.build());
        }
    }

//    @Operation(
//            summary = "Get All",
//            description = "Get all product"
//    )
//    @SecurityRequirement(name = "Authorization")
//    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<CommonResponse<List<ProductResponse>>> findAll() {
//        List<ProductResponse> listProduct = productService.findAll();
//        CommonResponse<List<ProductResponse>> commonResponse = CommonResponse.<List<ProductResponse>>builder()
//                .data(listProduct)
//                .message("Successfully get data")
//                .statusCode(HttpStatus.OK.value())
//                .build();
//        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
//    }

    @Operation(
            summary = "Get All",
            description = "Get all product"
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<ProductResponse>>> findAll(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "productName", required = false) String productName,
            @RequestParam(name = "price", required = false) Integer price,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "stock", required = false) Integer stock
    ) {
        SearchProductRequest searchRequest = SearchProductRequest.builder()
                .direction(direction)
                .sortBy(sortBy)
                .size(size)
                .page(page)
                .productName(productName)
                .description(description)
                .price(price)
                .stock(stock)
                .build();
        Page<ProductResponse> listProduct = productService.findAll(searchRequest);
        PagingResponse pagingResponse = PagingResponse.builder()
                .page(listProduct.getTotalPages() + 1)
                .size(listProduct.getSize())
                .hasPrevious(listProduct.hasPrevious())
                .hasNext(listProduct.hasNext())
                .totalElement(listProduct.getTotalElements())
                .totalPages(listProduct.getTotalPages())
                .build();
        CommonResponse<List<ProductResponse>> commonResponse = CommonResponse.<List<ProductResponse>>builder()
                .data(listProduct.getContent())
                .message("Successfully get data")
                .statusCode(HttpStatus.OK.value())
                .pagingResponse(pagingResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(
            summary = "Delete Product",
            description = "Delete product by id"
    )
    @SecurityRequirement(name = "Authorization")
    @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<String>> deleteById(@PathVariable String id) {
        productService.deleteById(id);
        CommonResponse<String> response = CommonResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success Delete Menu")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
