package com.enigma.laternak.repository;

import com.enigma.wmb_api.constant.UserRole;
import com.enigma.wmb_api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,String> {
    Optional<Role> findByRole(UserRole role);
}
