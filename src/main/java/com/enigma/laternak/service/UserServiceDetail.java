package com.enigma.laternak.service.impl;

import com.enigma.laternak.entity.Account;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserServiceDetail extends UserDetailsService {
    Account getUserById(String id);
    Account getByContext
}
