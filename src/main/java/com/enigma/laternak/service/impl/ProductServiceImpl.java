package com.enigma.laternak.service.impl;

import com.enigma.laternak.dto.request.ProductRequest;
import com.enigma.laternak.dto.request.SearchProductRequest;
import com.enigma.laternak.dto.request.UpdateProductRequest;
import com.enigma.laternak.dto.response.ProductResponse;
import com.enigma.laternak.entity.Product;
import com.enigma.laternak.entity.Store;
import com.enigma.laternak.repository.ProductRepository;
import com.enigma.laternak.repository.StoreRepository;
import com.enigma.laternak.service.ProductService;
import com.enigma.laternak.spesification.ProductSpesification;
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

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse create(ProductRequest request) {
        Store store=storeRepository.findById(request.getStoreId()).orElseThrow(()->new ResponseStatusException(HttpStatus.BAD_REQUEST, "Product not found"));
        Product product = Product.builder()
                .price(request.getPrice())
                .productName(request.getProductName())
                .stock(request.getStock())
                .description(request.getDescription())
                .store(store).build();
        return convertProductToProductResponse(productRepository.saveAndFlush(product));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product findById(String id) {
        return productRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id is not found"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse findOneById(String id) {
        return convertProductToProductResponse(findById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse update(UpdateProductRequest product) {
        Product currentProduct=findById(product.getId());
        currentProduct.setProductName(product.getProductName());
        currentProduct.setDescription(product.getDescription());
        currentProduct.setStock(product.getStock());
        currentProduct.setPrice(product.getPrice());
        return convertProductToProductResponse(productRepository.saveAndFlush(currentProduct));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(String id) {
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductResponse> findAll(SearchProductRequest request) {
        if (request.getPage() <= 0) request.setPage(1);
        Specification<Product> specification= ProductSpesification.getSpesification(request);
        Sort sort=Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable= PageRequest.of(request.getPage()-1, request.getSize(), sort);
        return productRepository.findAll(specification, pageable).map(this::convertProductToProductResponse);
    }

    private ProductResponse convertProductToProductResponse(Product product){
        return ProductResponse.builder()
                .id(product.getId())
                .price(product.getPrice())
                .productName(product.getProductName())
                .stock(product.getStock())
                .description(product.getDescription())
                .storeId(product.getStore().getId())
                .build();
    }
}
