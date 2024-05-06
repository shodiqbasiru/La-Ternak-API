package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.ProductRequest;
import com.enigma.laternak.dto.request.SearchProductRequest;
import com.enigma.laternak.dto.request.UpdateProductRequest;
import com.enigma.laternak.dto.response.CommonResponse;
import com.enigma.laternak.dto.response.PagingResponse;
import com.enigma.laternak.dto.response.ProductResponse;
import com.enigma.laternak.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiRoute.PRODUCT_API)
@Tag(name = "Product", description = "Api for product")
public class ProductController {
    private final ProductService productService;

    @Operation(
            summary = "Create Product",
            description = "Create a new product"
    )
    @SecurityRequirement(name = "Authorization")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<ProductResponse>> create(@RequestBody ProductRequest request){
        ProductResponse response=productService.create(request);
        CommonResponse<ProductResponse> commonResponse = CommonResponse.<ProductResponse>builder()
                .data(response)
                .message("Successfully create data")
                .statusCode(HttpStatus.CREATED.value())
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(commonResponse);
    }

    @Operation(
            summary = "Update Product",
            description = "Update product"
    )
    @SecurityRequirement(name = "Authorization")
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<String>> update(@RequestBody UpdateProductRequest request){
        productService.update(request);
        CommonResponse<String> commonResponse = CommonResponse.<String>builder()
                .data("Ok")
                .message("Successfully update data")
                .statusCode(HttpStatus.OK.value())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(commonResponse);
    }

    @Operation(
            summary = "Get All",
            description = "Get all product"
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<ProductResponse>>> findAll(
            @RequestParam(name="page", defaultValue = "1") Integer page,
            @RequestParam(name="size", defaultValue = "10") Integer size,
            @RequestParam(name="direction", defaultValue = "asc") String direction,
            @RequestParam(name="sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name="productName", required = false) String productName,
            @RequestParam(name="price", required = false) Integer price,
            @RequestParam(name="description", required = false) String description,
            @RequestParam(name="stock", required = false) Integer stock
    ){
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
    public ResponseEntity<CommonResponse<String>> deleteById(@PathVariable String id){
        productService.deleteById(id);
        CommonResponse<String> response=CommonResponse.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Success Delete Menu")
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
