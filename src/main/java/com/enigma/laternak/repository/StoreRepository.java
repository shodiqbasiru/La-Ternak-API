package com.enigma.laternak.repository;

import com.enigma.laternak.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, String>{
    Optional<Store> findByEmail(String email);
}
