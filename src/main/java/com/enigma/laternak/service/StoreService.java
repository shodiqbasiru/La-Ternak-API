package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.PaginationStoreRequest;
import com.enigma.laternak.dto.request.StoreRequest;
import com.enigma.laternak.dto.response.StoreResponse;
import com.enigma.laternak.entity.Store;
import org.springframework.data.domain.Page;

public interface StoreService {
    void createStore(Store request);

    Store getByEmail(String email);

    Store getById(String id);

    StoreResponse getStoreById(String id);

    Page<StoreResponse> getAll(PaginationStoreRequest request);

    StoreResponse updateStore(StoreRequest store);

    void setVerifyStore(Store store);

    void deleteAccountSeller(String id);
}
