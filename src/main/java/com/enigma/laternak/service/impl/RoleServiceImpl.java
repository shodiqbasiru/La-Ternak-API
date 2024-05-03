package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.UserRole;
import com.enigma.laternak.entity.Role;
import com.enigma.laternak.repository.RoleRepository;
import com.enigma.laternak.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Role getOrSave(UserRole role) {
        return roleRepository.findByRole(role).orElseGet(() -> roleRepository.saveAndFlush(Role.builder().role(role).build()));
    }
}
