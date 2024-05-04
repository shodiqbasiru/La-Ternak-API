package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.AuthRequest;
import com.enigma.laternak.dto.request.LoginRequest;
import com.enigma.laternak.dto.response.LoginResponse;
import com.enigma.laternak.dto.response.RegisterResponse;

public interface AuthService {
    RegisterResponse register(AuthRequest request);
    LoginResponse login(LoginRequest request);
    boolean validateToken();
}
