package com.enigma.laternak.service;

import com.enigma.laternak.entity.Store;

public interface StoreService {
    void createStore(Store request);

    Store getByEmail(String email);

    void updateStore(Store store);
}
