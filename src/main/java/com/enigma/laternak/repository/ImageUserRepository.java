package com.enigma.laternak.repository;

import com.enigma.laternak.entity.ImageUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageUserRepository extends JpaRepository<ImageUser, String> {
}
