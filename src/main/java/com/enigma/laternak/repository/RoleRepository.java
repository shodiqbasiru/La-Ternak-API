package com.enigma.laternak.repository;

import com.enigma.laternak.constant.UserRole;
import com.enigma.laternak.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,String> {
    Optional<Role> findByRole(UserRole role);
}
