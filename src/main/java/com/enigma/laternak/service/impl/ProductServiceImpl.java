package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.ProductRequest;
import com.enigma.laternak.dto.request.SearchProductRequest;
import com.enigma.laternak.dto.request.UpdateProductRequest;
import com.enigma.laternak.dto.response.ImageProductResponse;
import com.enigma.laternak.dto.response.ProductResponse;
import com.enigma.laternak.dto.response.ReviewResponse;
import com.enigma.laternak.entity.ImageProduct;
import com.enigma.laternak.entity.Product;
import com.enigma.laternak.entity.Review;
import com.enigma.laternak.entity.Store;
import com.enigma.laternak.repository.ProductRepository;
import com.enigma.laternak.service.ImageProductService;
import com.enigma.laternak.service.ProductService;
import com.enigma.laternak.service.StoreService;
import com.enigma.laternak.spesification.ProductSpecification;
import com.enigma.laternak.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final StoreService storeService;
    private final ValidationUtil validationUtil;
    private final ImageProductService imageProductService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ProductResponse create(ProductRequest request) {
        if (request.getImages().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image is required");

        validationUtil.validate(request);

        Store store = storeService.getById(request.getStoreId());
        Product product = Product.builder()
                .productName(request.getProductName())
                .description(request.getDescription())
                .stock(request.getStock())
                .price(request.getPrice())
                .store(store)
                .isActive(true)
                .build();


            List<ImageProduct> imageProduct = imageProductService.create(request.getImages(), product);
            product.setImages(imageProduct);
            productRepository.saveAndFlush(product);

        return convertProductToProductResponse(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product findById(String id) {
        return productRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Id is not found"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse findOneById(String id) {
        return convertProductToProductResponse(findById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse update(UpdateProductRequest request) {

        Product currentProduct = findById(request.getId());
        List<ImageProduct> imageProducts = currentProduct.getImages();

        currentProduct.setProductName(request.getProductName());
        currentProduct.setDescription(request.getDescription());
        currentProduct.setStock(request.getStock());
        currentProduct.setPrice(request.getPrice());
        if (request.getImages() != null) {
            imageProducts.forEach(imageProduct -> imageProductService.deleteById(imageProduct.getId()));
            List<ImageProduct> imageProduct = imageProductService.create(request.getImages(), currentProduct);
            currentProduct.setImages(imageProduct);
        } else {
            productRepository.saveAndFlush(currentProduct);
        }

        return convertProductToProductResponse(currentProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        Product currentProduct = findById(id);
        currentProduct.setIsActive(false);
    }

    @Override
    public Page<ProductResponse> findAll(SearchProductRequest request) {
        if (request.getPage() <= 0) request.setPage(1);
        Specification<Product> specification = ProductSpecification.getSpecification(request);
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize(), sort);
        return productRepository.findAll(specification, pageable).map(this::convertProductToProductResponse);
    }

    @Override
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream().map(this::convertProductToProductResponse).toList();
    }

    private ProductResponse convertProductToProductResponse(Product product) {
        List<Review> reviews = product.getReviews() != null ? product.getReviews() : List.of();
        List<ImageProductResponse> imageProductResponses = product.getImages().stream()
                .map(imageProduct -> ImageProductResponse.builder()
                        .url(ApiRoute.IMAGE_PRODUCT_API + "/" + imageProduct.getId())
                        .name(imageProduct.getName())
                        .build())
                .toList();
        List<ReviewResponse> reviewResponses = reviews.stream().map(review -> ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .userName(review.getUser().getCustomerName())
                .productId(review.getProduct().getId())
                .build()).toList();
        return ProductResponse.builder()
                .id(product.getId())
                .price(product.getPrice())
                .productName(product.getProductName())
                .stock(product.getStock())
                .description(product.getDescription())
                .storeId(product.getStore().getId())
                .reviews(reviewResponses)
                .images(imageProductResponses)
                .build();
    }
}
