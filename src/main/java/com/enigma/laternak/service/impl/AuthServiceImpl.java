package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.UserRole;
import com.enigma.laternak.dto.request.AuthRequest;
import com.enigma.laternak.dto.request.LoginRequest;
import com.enigma.laternak.dto.response.LoginResponse;
import com.enigma.laternak.dto.response.RegisterResponse;
import com.enigma.laternak.entity.Account;
import com.enigma.laternak.entity.Role;
import com.enigma.laternak.entity.User;
import com.enigma.laternak.repository.AccountRepository;
import com.enigma.laternak.service.AuthService;
import com.enigma.laternak.service.JwtService;
import com.enigma.laternak.service.RoleService;
import com.enigma.laternak.service.UserService;
import com.enigma.laternak.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final UserService userService;
    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ValidationUtil validation;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterResponse register(AuthRequest request) {
        validation.validate(request);

        Role role = roleService.getOrSave(UserRole.ROLE_CUSTOMER);
        String hashPassword = passwordEncoder.encode(request.getPassword());

        Account account = Account.builder()
                .username(request.getUsername())
                .password(hashPassword)
                .roles(List.of(role))
                .isEnable(true)
                .build();
        accountRepository.saveAndFlush(account);

        User user = User.builder()
                .customerName(request.getName())
                .numberPhone(request.getPhoneNumber())
                .account(account)
                .build();
        userService.create(user);

        List<String> roles = account.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return RegisterResponse.builder()
                .username(account.getUsername())
                .roles(roles)
                .build();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );
        Authentication authenticate = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        Account account = (Account) authenticate.getPrincipal();
        String jwtToken = jwtService.generateToken(account);
        return LoginResponse.builder()
                .username(account.getUsername())
                .roles(account.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .token(jwtToken)
                .build();
    }

    @Override
    public boolean validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountRepository.findByUsername(authentication.getPrincipal().toString()).orElse(null);
        return account != null;
    }
}
