package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.AuthRequest;
import com.enigma.laternak.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(AuthRequest request);
}
