package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.PaginationStoreRequest;
import com.enigma.laternak.dto.response.StoreResponse;
import com.enigma.laternak.entity.Store;
import org.springframework.data.domain.Page;

public interface StoreService {
    void createStore(Store request);

    Store getByEmail(String email);

    Page<StoreResponse> getAll(PaginationStoreRequest request);

    void updateStore(Store store);
}
