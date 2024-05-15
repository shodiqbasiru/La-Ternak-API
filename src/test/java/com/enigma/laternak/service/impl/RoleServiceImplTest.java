package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.UserRole;
import com.enigma.laternak.entity.Role;
import com.enigma.laternak.repository.RoleRepository;
import com.enigma.laternak.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {
    @Mock
    private RoleRepository roleRepository;
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl(roleRepository);
    }

    @Test
    void shouldReturnRoleWhenGetOrSave() {
        Role role = new Role();
        role.setId("1");
        role.setRole(UserRole.ROLE_ADMIN);

        Mockito.when(roleRepository.findByRole(UserRole.ROLE_ADMIN)).thenReturn(Optional.of(role));

        Role result = roleService.getOrSave(UserRole.ROLE_ADMIN);
        assertEquals(role, result);
    }
}