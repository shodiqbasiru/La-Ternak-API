package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.Message;
import com.enigma.laternak.constant.UserRole;
import com.enigma.laternak.dto.request.AuthRequest;
import com.enigma.laternak.dto.request.EmailRequest;
import com.enigma.laternak.dto.request.LoginRequest;
import com.enigma.laternak.dto.request.RegisterSellerRequest;
import com.enigma.laternak.dto.response.LoginResponse;
import com.enigma.laternak.dto.response.LoginSellerResponse;
import com.enigma.laternak.dto.response.RegisterResponse;
import com.enigma.laternak.dto.response.RegisterSellerResponse;
import com.enigma.laternak.entity.Account;
import com.enigma.laternak.entity.Role;
import com.enigma.laternak.entity.Store;
import com.enigma.laternak.entity.User;
import com.enigma.laternak.repository.AccountRepository;
import com.enigma.laternak.service.*;
import com.enigma.laternak.util.ResponseMessage;
import com.enigma.laternak.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final AccountRepository accountRepository;
    private final RoleService roleService;
    private final UserService userService;
    private final JwtService jwtService;
    private final UserServiceDetail userServiceDetail;
    private final StoreService storeService;
    private final OtpGeneratorService otpService;
    private final EmailService emailService;

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final ValidationUtil validation;

    @Value("${la_ternak.username.admin}")
    private String adminUsername;

    @Value("${la_ternak.password.admin}")
    private String adminPassword;

    @Transactional(rollbackFor = Exception.class)
    @PostConstruct
    public void initAdmin() {
        Optional<Account> account = accountRepository.findByUsername(adminUsername);
        if (account.isPresent()) return;

        Role role = roleService.getOrSave(UserRole.ROLE_ADMIN);
        String hashPassword = passwordEncoder.encode(adminPassword);

        Account admin = Account.builder()
                .username("admin")
                .password(hashPassword)
                .roles(List.of(role))
                .isEnable(true)
                .build();
        accountRepository.saveAndFlush(admin);
    }

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
                .phoneNumber(request.getPhoneNumber())
                .account(account)
                .build();
        userService.create(user);

        List<String> roles = account.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return RegisterResponse.builder()
                .username(account.getUsername())
                .roles(roles)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public RegisterSellerResponse registerSeller(RegisterSellerRequest request) {
        validation.validate(request);

        Role role = roleService.getOrSave(UserRole.ROLE_SELLER);

        Account account = userServiceDetail.getByContext();
        List<Role> roleList = account.getRoles();
        roleList.add(role);
        account.setRoles(roleList);

        User user = userService.getById(account.getUser().getId());
        String otp = otpService.generateOtp();

        Store store = Store.builder()
                .storeName(request.getStoreName())
                .address(request.getAddress())
                .email(request.getEmail())
                .user(user)
                .otp(otp)
                .otpGenerateTime(LocalDateTime.now())
                .isVerified(false)
                .isActive(false)
                .build();
        storeService.createStore(store);

        EmailRequest emailRequest = EmailRequest.builder()
                .to(store.getEmail())
                .subject("Verification Account Seller")
                .body("""
                         <h1>Verification Account Seller</h1>
                         <p>Click this link to verify your account</p>
                        <div>
                             <a href="http://localhost:8080/api/auth/verify-account-seller?email=%s&otp=%s" target="_blank"> Click the link to verify</a>
                         </div>
                        """.formatted(store.getEmail(), otp))
                .build();

        try {
            emailService.sendEmail(emailRequest);
        } catch (MessagingException e) {
            throw ResponseMessage.error(HttpStatus.BAD_REQUEST, Message.ERROR_UNABLE_SEND_EMAIL);
        }

        // variable for response
        List<String> roles = account.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        String username = account.getUsername();
        String email = store.getEmail();
        String storeName = store.getStoreName();
        boolean isVerified = store.isVerified();

        return RegisterSellerResponse.builder()
                .username(username)
                .email(email)
                .storeName(storeName)
                .isVerified(isVerified)
                .roles(roles)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );
        Authentication authenticate = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        Account account = (Account) authenticate.getPrincipal();
        if (!account.getIsEnable()) {
            throw ResponseMessage.error(HttpStatus.UNAUTHORIZED, Message.ERROR_ACCOUNT_NOT_ACTIVE);
        }

        // variable for response
        String userId = (account.getUser() != null) ? account.getUser().getId() : null;
        String storeId = (account.getUser() != null && account.getUser().getStore() != null) ? account.getUser().getStore().getId() : null;
        String username = account.getUsername();
        List<String> roles = account.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        String jwtToken = jwtService.generateToken(account);

        return LoginResponse.builder()
                .userId(userId)
                .storeId(storeId)
                .username(username)
                .roles(roles)
                .token(jwtToken)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public LoginSellerResponse loginSeller(LoginRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        );
        Authentication authenticate = authenticationManager.authenticate(authentication);
        SecurityContextHolder.getContext().setAuthentication(authenticate);


        Account account = (Account) authenticate.getPrincipal();
        Store store = account.getUser().getStore();

        if (store == null) {
            throw ResponseMessage.error(HttpStatus.UNAUTHORIZED, Message.ERROR_ACCOUNT_NOT_SELLER);
        }
        if (!store.isActive() && !store.isVerified()) {
            throw ResponseMessage.error(HttpStatus.UNAUTHORIZED, Message.ERROR_ACCOUNT_NOT_ACTIVE);
        }

        // variable for response
        String username = account.getUsername();
        String jwtToken = jwtService.generateToken(account);
        String email = account.getUser().getStore().getEmail();
        List<String> role = account.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return LoginSellerResponse.builder()
                .username(username)
                .email(email)
                .role(role)
                .token(jwtToken)
                .build();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String verifyAccountSeller(String email, String otp) {
        Store store = storeService.getByEmail(email);
        if (store.getOtp().equals(otp) && Duration.between(store.getOtpGenerateTime(), LocalDateTime.now()).getSeconds() < 60) {
            store.setVerified(true);
            store.setActive(true);
            storeService.setVerifyStore(store);
            return "Your account has been verified";
        } else {
            return "Your account is not verified";
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String regenerateOtp(String email) {
        Store store = storeService.getByEmail(email);
        String otp = otpService.generateOtp();

        EmailRequest emailRequest = EmailRequest.builder()
                .to(store.getEmail())
                .subject("Verification Account Seller")
                .body("""
                         <h1>Verification Account Seller</h1>
                         <p>Click this link to verify your account</p>
                        <div>
                             <a href="http://localhost:8080/api/auth/verify-account-seller?email=%s&otp=%s" target="_blank"> Click the link to verify</a>
                         </div>
                        """.formatted(store.getEmail(), otp))
                .build();

        try {
            emailService.sendEmail(emailRequest);
        } catch (MessagingException e) {
            throw ResponseMessage.error(HttpStatus.BAD_REQUEST, Message.ERROR_UNABLE_SEND_EMAIL);
        }

        store.setOtp(otp);
        store.setOtpGenerateTime(LocalDateTime.now());

        return "Otp has been sent to your email, please verify account within 1 minute";
    }


    @Override
    public boolean validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Account account = accountRepository.findByUsername(authentication.getPrincipal().toString()).orElse(null);
        return account != null;
    }
}
