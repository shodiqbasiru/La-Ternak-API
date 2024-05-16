package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.constant.Message;
import com.enigma.laternak.dto.request.PaginationStoreRequest;
import com.enigma.laternak.dto.request.StoreRequest;
import com.enigma.laternak.dto.response.ImageResponse;
import com.enigma.laternak.dto.response.ProductResponse;
import com.enigma.laternak.dto.response.StoreResponse;
import com.enigma.laternak.entity.ImageStore;
import com.enigma.laternak.entity.Store;
import com.enigma.laternak.repository.StoreRepository;
import com.enigma.laternak.service.ImageStoreService;
import com.enigma.laternak.service.StoreService;
import com.enigma.laternak.util.ResponseMessage;
import com.enigma.laternak.util.ValidationUtil;
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
    private final ImageStoreService imageStoreService;
    private final ValidationUtil validationUtil;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createStore(Store request) {
        storeRepository.saveAndFlush(request);
    }

    @Transactional(readOnly = true)
    @Override
    public Store getByEmail(String email) {
        return storeRepository.findByEmail(email)
                .orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_EMAIL_NOT_FOUND));
    }

    @Override
    public Store getById(String id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_STORE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public StoreResponse getStoreById(String id) {
        Store store = getById(id);
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
                                .build())
                        .toList())
                .imageStore(store.getImageStore() == null ? null : ImageResponse.builder()
                        .url(ApiRoute.IMAGE_STORE_API + "/" + store.getImageStore().getId())
                        .name(store.getImageStore().getName())
                        .build())
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
    public StoreResponse updateStore(StoreRequest request) {
        validationUtil.validate(request);

        Store currentStore = getById(request.getId());
        ImageStore imgOld = currentStore.getImageStore();
        currentStore.setStoreName(request.getStoreName());
        currentStore.setAddress(request.getAddress());
        if (request.getImage() != null) {
            ImageStore imageStore = imageStoreService.create(request.getImage());
            currentStore.setImageStore(imageStore);
            if (imgOld != null) {
                imageStoreService.deleteById(imgOld.getId());
            }
        }
        storeRepository.saveAndFlush(currentStore);
        return StoreResponse.builder()
                .id(currentStore.getId())
                .storeName(currentStore.getStoreName())
                .email(currentStore.getEmail())
                .address(currentStore.getAddress())
                .isActive(currentStore.isActive())
                .productDetails(currentStore.getProducts() == null ? null : currentStore.getProducts().stream()
                        .map(product -> ProductResponse.builder()
                                .id(product.getId())
                                .productName(product.getProductName())
                                .price(product.getPrice())
                                .stock(product.getStock())
                                .description(product.getDescription())
                                .storeId(product.getStore().getId())
                                .build())
                        .toList())
                .imageStore(ImageResponse.builder()
                        .url(ApiRoute.IMAGE_STORE_API + "/" + currentStore.getImageStore().getId())
                        .name(currentStore.getImageStore().getName())
                        .build())
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void setVerifyStore(Store store) {
        storeRepository.saveAndFlush(store);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteAccountSeller(String id) {
        Store currentStore = getById(id);
        currentStore.setVerified(false);
        currentStore.setActive(false);
    }
}
