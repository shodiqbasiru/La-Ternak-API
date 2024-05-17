package com.enigma.laternak.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.enigma.laternak.constant.UserRole;
import com.enigma.laternak.entity.Account;
import com.enigma.laternak.entity.Role;
import com.enigma.laternak.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {
    private String JWT_SECRET = "jwtSecret";
    private String ISSUER = "issuer";
    private long JWT_EXPIRATION = 0;
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtServiceImpl(JWT_SECRET, ISSUER, JWT_EXPIRATION);
    }

    @Test
    void shouldGenerateToken() {
        List<Role> roles = List.of(new Role("1",UserRole.ROLE_ADMIN));
        Account account = new Account();
        account.setId("1");
        account.setUsername("username");
        account.setPassword("password");
        account.setRoles(roles);

        Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);
        String newToken = JWT.create()
                .withSubject(account.getId())
                .withClaim("roles", account.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(JWT_EXPIRATION))
                .withIssuer(ISSUER)
                .sign(algorithm);

        String token = jwtService.generateToken(account);
        assertEquals(newToken, token);
    }

    @Test
    void shouldReturnTrueWhenTokenIsValid() {
        List<Role> roles = List.of(new Role("1",UserRole.ROLE_ADMIN));
        Account account = new Account();
        account.setId("1");
        account.setUsername("username");
        account.setPassword("password");
        account.setRoles(roles);

        Algorithm algorithm = Algorithm.HMAC512(JWT_SECRET);
        String newToken = JWT.create()
                .withSubject(account.getId())
                .withClaim("roles", account.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withIssuedAt(Instant.now())
                .withExpiresAt(Instant.now().plusSeconds(JWT_EXPIRATION))
                .withIssuer(ISSUER)
                .sign(algorithm);

        boolean result = jwtService.verifyJwtToken(newToken);

        assertTrue(result);
    }


    @Test
    void getClaimsByToken() {
    }
}