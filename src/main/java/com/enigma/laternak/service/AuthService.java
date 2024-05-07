package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.AuthRequest;
import com.enigma.laternak.dto.request.LoginRequest;
import com.enigma.laternak.dto.request.LoginSellerRequest;
import com.enigma.laternak.dto.request.RegisterSellerRequest;
import com.enigma.laternak.dto.response.LoginResponse;
import com.enigma.laternak.dto.response.LoginSellerResponse;
import com.enigma.laternak.dto.response.RegisterResponse;
import com.enigma.laternak.dto.response.RegisterSellerResponse;
import org.springframework.web.servlet.view.RedirectView;

public interface AuthService {
    RegisterResponse register(AuthRequest request);
    RegisterSellerResponse registerSeller(RegisterSellerRequest request);
    LoginResponse login(LoginRequest request);
    LoginSellerResponse loginSeller(LoginRequest request);
    String verifyAccountSeller(String email, String otp);
    String regenerateOtp(String email);

    boolean validateToken();
}
