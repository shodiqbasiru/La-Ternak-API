package com.enigma.laternak.repository;

import com.enigma.laternak.entity.ImageStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageStoreRepository extends JpaRepository<ImageStore, String> {
}
