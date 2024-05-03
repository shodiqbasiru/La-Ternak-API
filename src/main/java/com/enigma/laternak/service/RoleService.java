package com.enigma.laternak.service;

import com.enigma.wmb_api.constant.UserRole;
import com.enigma.wmb_api.entity.Role;

public interface RoleService {
    Role getOrSave(UserRole role);
}
