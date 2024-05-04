package com.enigma.laternak.security;

import com.enigma.laternak.entity.Account;
import com.enigma.laternak.entity.JwtClaims;
import com.enigma.laternak.service.JwtService;
import com.enigma.laternak.service.UserServiceDetail;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserServiceDetail userServiceDetail;
    final String AUTH_HEADER = "Authorization";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            String bearerToken = request.getHeader(AUTH_HEADER);
            JwtClaims claimsByToken = jwtService.getClaimsByToken(bearerToken);
            Account account = userServiceDetail.getUserById(claimsByToken.getAccountId());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    account.getUsername(),
                    null,
                    account.getAuthorities()
            );
            authentication.setDetails(WebApplicationInitializer.class);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            log.error("Cannot set user authenticate: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
