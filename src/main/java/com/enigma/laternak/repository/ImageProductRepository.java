package com.enigma.laternak.repository;

import com.enigma.laternak.entity.ImageProduct;
import com.enigma.laternak.entity.ImageUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageProductRepository extends JpaRepository<ImageProduct, String> {
}
