package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.ProductRequest;
import com.enigma.laternak.dto.request.SearchProductRequest;
import com.enigma.laternak.dto.request.UpdateProductRequest;
import com.enigma.laternak.dto.response.ProductResponse;
import com.enigma.laternak.entity.Product;
import org.springframework.data.domain.Page;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    Product findById(String id);
    ProductResponse findOneById(String id);
    ProductResponse update(UpdateProductRequest product);
    void deleteById(String id);
    Page<ProductResponse> findAll(SearchProductRequest request);
}
