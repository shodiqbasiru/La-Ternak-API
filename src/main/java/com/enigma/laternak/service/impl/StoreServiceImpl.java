package com.enigma.laternak.service.impl;

import com.enigma.laternak.entity.Store;
import com.enigma.laternak.repository.StoreRepository;
import com.enigma.laternak.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createStore(Store request) {
        storeRepository.saveAndFlush(request);
    }

    @Override
    public Store getByEmail(String email) {
        return storeRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("email not found"));
    }

    @Override
    public void updateStore(Store store) {
        storeRepository.saveAndFlush(store);
    }
}
