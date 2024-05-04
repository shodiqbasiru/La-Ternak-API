package com.enigma.laternak.service;

import com.enigma.laternak.entity.Account;
import com.enigma.laternak.entity.JwtClaims;

public interface JwtService {
    String generateToken(Account account);
    boolean verifyJwtToken(String token);
    JwtClaims getClaimsByToken(String token);
}
