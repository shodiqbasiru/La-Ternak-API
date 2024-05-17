package com.enigma.laternak.service;

import com.enigma.laternak.constant.UserRole;
import com.enigma.laternak.entity.Role;

public interface RoleService {
    Role getOrSave(UserRole role);
}
