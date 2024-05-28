package com.enigma.laternak.service.impl;

import com.enigma.laternak.dto.request.ProductRequest;
import com.enigma.laternak.dto.request.ProductSpecificationRequest;
import com.enigma.laternak.dto.request.SearchProductRequest;
import com.enigma.laternak.dto.request.UpdateProductRequest;
import com.enigma.laternak.dto.response.ImageProductResponse;
import com.enigma.laternak.dto.response.ProductResponse;
import com.enigma.laternak.entity.*;
import com.enigma.laternak.repository.ProductRepository;
import com.enigma.laternak.service.ImageProductService;
import com.enigma.laternak.service.ProductService;
import com.enigma.laternak.service.StoreService;
import com.enigma.laternak.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private StoreService storeService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ImageProductService imageProductService;
    @Mock
    private ValidationUtil validationUtil;
    private ProductService productService;


    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, storeService, validationUtil, imageProductService);
    }

    @Test
    void shouldReturnProductWhenCreate() {
        List<MultipartFile> images = List.of(
                Mockito.mock(MultipartFile.class),
                Mockito.mock(MultipartFile.class),
                Mockito.mock(MultipartFile.class)
        );

        ProductRequest request = ProductRequest.builder()
                .productName("product-1")
                .description("desc")
                .stock(1)
                .price(1000)
                .storeId("store-1")
                .images(images)
                .build();

        Store store = new Store();
        store.setId(request.getStoreId());
        Mockito.when(storeService.getById(store.getId())).thenReturn(store);

        Product product = Product.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .stock(request.getStock())
                .price(request.getPrice())
                .store(store)
                .isActive(true)

                .build();

        Mockito.when(productRepository.saveAndFlush(Mockito.any())).thenReturn(product);

        ProductResponse result = productService.create(request);

        Mockito.verify(validationUtil).validate(request);
        Mockito.verify(storeService).getById(request.getStoreId());
        Mockito.verify(productRepository).saveAndFlush(Mockito.any());

        assertEquals(product.getProductName(), result.getProductName());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getStock(), result.getStock());
        assertEquals(product.getPrice(), result.getPrice());
    }

    @Test
    void shouldReturnErrorWhenCreateWithEmptyImages() {
        ProductRequest request = ProductRequest.builder()
                .productName("product-1")
                .description("desc")
                .stock(1)
                .price(1000)
                .storeId("store-1")
                .images(List.of())
                .build();

        assertThrows(Exception.class, () -> productService.create(request));
    }

    @Test
    void shouldReturnProductWhenFindById() {
        Product product = new Product();
        product.setId("1");
        Mockito.when(productRepository.findById(product.getId())).thenReturn(java.util.Optional.of(product));

        Product result = productService.findById(product.getId());

        Mockito.verify(productRepository).findById(product.getId());

        assertEquals(product, result);
    }

    @Test
    void shouldReturnErrorNotFoundWhenFindById() {
        Product product = new Product();
        product.setId("1");
        Mockito.when(productRepository.findById(product.getId())).thenReturn(java.util.Optional.empty());

        assertThrows(Exception.class, () -> productService.findById(product.getId()));
    }

    @Test
    void shouldReturnProductResponseWhenFindOneById() {
        Product product = new Product();
        product.setId("1");
        product.setProductName("product-1");
        product.setDescription("desc");
        product.setStock(1);
        product.setPrice(1000);
        product.setImages(Collections.emptyList());
        product.setStore(new Store());

        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        ProductResponse result = productService.findOneById(product.getId());

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getProductName(), result.getProductName());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getStock(), result.getStock());
        assertEquals(product.getPrice(), result.getPrice());
    }

    @Test
    void shouldReturnProductResponseWhenUpdate() {
        List<MultipartFile> images = List.of(
                Mockito.mock(MultipartFile.class),
                Mockito.mock(MultipartFile.class),
                Mockito.mock(MultipartFile.class)
        );

        UpdateProductRequest request = UpdateProductRequest.builder()
                .id("1")
                .productName("product-1")
                .description("desc")
                .stock(1)
                .price(1000)
                .images(images)
                .build();

        Product product = Product.builder()
                .id(request.getId())
                .productName(request.getProductName())
                .description(request.getDescription())
                .stock(request.getStock())
                .price(request.getPrice())
                .store(new Store())
                .isActive(true)
                .images(request.getImages().stream().map(image -> ImageProduct.builder()
                        .id("1")
                        .name(image.getOriginalFilename())
                        .contentType(image.getContentType())
                        .build())
                        .toList())
                .build();

        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Mockito.when(imageProductService.create(request.getImages(), product)).thenReturn(Collections.emptyList());

        ProductResponse result = productService.update(request);

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getProductName(), result.getProductName());
        assertEquals(product.getDescription(), result.getDescription());
        assertEquals(product.getStock(), result.getStock());
        assertEquals(product.getPrice(), result.getPrice());
    }

    @Test
    void shouldReturnFalseWhenDeleteById() {
        Product product = new Product();
        product.setId("1");
        Mockito.when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        productService.deleteById(product.getId());

        Mockito.verify(productRepository).findById(product.getId());
        assertFalse(product.getIsActive());
    }

    @Test
    void shouldReturnProductWhenFindAllWithParams() {
        SearchProductRequest request = new SearchProductRequest();
        request.setPage(1);
        request.setSize(10);
        request.setSortBy("productName");
        request.setDirection("asc");
        request.setProductName("product-1");
        request.setPrice(1000);
        request.setStock(1);
        request.setDescription("desc");

        List<Review> reviews = List.of(
                Review.builder()
                        .id("1")
                        .rating(5.0)
                        .comment("good")
                        .user(new User())
                        .product(new Product())
                        .build()
        );

        Product product = new Product();
        product.setId("1");
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setStock(request.getStock());
        product.setPrice(request.getPrice());
        product.setImages(Collections.emptyList());
        product.setStore(new Store());
        product.setReviews(reviews);

        Page<Product> page = new PageImpl<>(Collections.singletonList(product));

        Mockito.when(productRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(page);

        Page<ProductResponse> result = productService.findAll(request);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(product.getId(), result.getContent().get(0).getId());

    }

    @Test
    void shouldSetPage1WhenRequestGetPageIs0() {
        SearchProductRequest request = new SearchProductRequest();
        request.setPage(0);
        request.setSize(10);
        request.setSortBy("productName");
        request.setDirection("asc");
        request.setProductName("product-1");
        request.setPrice(1000);
        request.setStock(1);
        request.setDescription("desc");

        List<Review> reviews = List.of(
                Review.builder()
                        .id("1")
                        .rating(5.0)
                        .comment("good")
                        .user(new User())
                        .product(new Product())
                        .build()
        );

        Product product = new Product();
        product.setId("1");
        product.setProductName(request.getProductName());
        product.setDescription(request.getDescription());
        product.setStock(request.getStock());
        product.setPrice(request.getPrice());
        product.setImages(Collections.emptyList());
        product.setStore(new Store());
        product.setReviews(reviews);

        Page<Product> page = new PageImpl<>(Collections.singletonList(product));

        Mockito.when(productRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(page);

        Page<ProductResponse> result = productService.findAll(request);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(product.getId(), result.getContent().get(0).getId());
    }

    @Test
    void shouldReturnProductWhenFindAllWithoutParams() {
        List<Review> reviews = List.of(
                Review.builder()
                        .id("1")
                        .rating(5.0)
                        .comment("good")
                        .user(new User())
                        .product(new Product())
                        .build()
        );

        Product product = new Product();
        product.setId("1");
        product.setProductName("product-1");
        product.setDescription("desc");
        product.setStock(1);
        product.setPrice(1000);
        product.setImages(Collections.emptyList());
        product.setStore(new Store());
        product.setReviews(reviews);

        Mockito.when(productRepository.findAll()).thenReturn(Collections.singletonList(product));

        List<ProductResponse> result = productService.findAll(ProductSpecificationRequest.builder().build());

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}