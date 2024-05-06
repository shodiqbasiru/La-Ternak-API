package com.enigma.laternak.service.impl;

import com.enigma.laternak.dto.request.PaginationStoreRequest;
import com.enigma.laternak.dto.response.ProductResponse;
import com.enigma.laternak.dto.response.StoreResponse;
import com.enigma.laternak.entity.Store;
import com.enigma.laternak.repository.StoreRepository;
import com.enigma.laternak.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createStore(Store request) {
        storeRepository.saveAndFlush(request);
    }

    @Transactional(readOnly = true)
    @Override
    public Store getByEmail(String email) {
        return storeRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "email not found"));
    }

    @Transactional(readOnly = true)
    @Override
    public StoreResponse getById(String id) {
        Store store = storeRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "store not found"));
        return StoreResponse.builder()
                .id(store.getId())
                .storeName(store.getStoreName())
                .email(store.getEmail())
                .address(store.getAddress())
                .isActive(store.isActive())
                .productDetails(store.getProducts() == null ? null : store.getProducts().stream().map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .productName(product.getProductName())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .description(product.getDescription())
                        .storeId(product.getStore().getId())
                        .build()).toList())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<StoreResponse> getAll(PaginationStoreRequest request) {
        if (request.getPage() <= 0) request.setPage(1);

        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);

        return storeRepository.findAll(pageable).map(store -> StoreResponse.builder()
                .id(store.getId())
                .storeName(store.getStoreName())
                .email(store.getEmail())
                .address(store.getAddress())
                .isActive(store.isActive())
                .productDetails(store.getProducts() == null ? null : store.getProducts().stream().map(product -> ProductResponse.builder()
                        .id(product.getId())
                        .productName(product.getProductName())
                        .price(product.getPrice())
                        .stock(product.getStock())
                        .description(product.getDescription())
                        .storeId(product.getStore().getId())
                        .build()).toList())
                .build());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStore(Store store) {
        storeRepository.saveAndFlush(store);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteAccountSeller(String id) {
        Store currentStore = storeRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store Not Found"));
        currentStore.setVerified(false);
        currentStore.setActive(false);
    }
}
