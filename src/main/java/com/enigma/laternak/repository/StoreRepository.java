package com.enigma.laternak.repository;

import com.enigma.laternak.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, String> {
}
