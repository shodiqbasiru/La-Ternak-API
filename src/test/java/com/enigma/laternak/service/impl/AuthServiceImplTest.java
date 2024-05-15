package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.UserRole;
import com.enigma.laternak.dto.request.AuthRequest;
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
import com.enigma.laternak.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RoleService roleService;
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserServiceDetail userServiceDetail;
    @Mock
    private StoreService storeService;
    @Mock
    private OtpGeneratorService otpService;
    @Mock
    private EmailService emailService;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ValidationUtil validation;
    private AuthService authService;

    private String adminUsername = "admin";

    private String adminPassword = "admin";

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(accountRepository, roleService, userService, jwtService, userServiceDetail, storeService, otpService, emailService, authenticationManager, passwordEncoder, validation);
    }

   /* @Test
    void shouldInitAdmin() {
        Account account = new Account();
        account.setUsername(adminUsername);
        account.setPassword(adminPassword);

        Mockito.when(accountRepository.findByUsername(adminUsername)).thenReturn(Optional.of(account));

        Role role = new Role();
        role.setId("1");
        role.setRole(UserRole.ROLE_ADMIN);

        Mockito.when(roleService.getOrSave(UserRole.ROLE_ADMIN)).thenReturn(role);

        Account admin = new Account();
        admin.setUsername(adminUsername);
        admin.setPassword(adminPassword);
        admin.setRoles(List.of(role));

        Mockito.when(accountRepository.saveAndFlush(Mockito.any())).thenReturn(admin);

        authService.initAdmin();

        Mockito.verify(accountRepository).findByUsername(adminUsername);
    }*/

    @Test
    void shouldReturnRegisterResponseWhenRegister() {
        AuthRequest request = new AuthRequest();
        request.setUsername("username");
        request.setPassword("password");

        validation.validate(request);

        Role role = new Role();
        role.setId("1");
        role.setRole(UserRole.ROLE_CUSTOMER);

        Mockito.when(roleService.getOrSave(UserRole.ROLE_CUSTOMER)).thenReturn(role);
        String hashPassword = passwordEncoder.encode(request.getPassword());

        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(hashPassword);
        account.setRoles(List.of(role));

        Mockito.when(accountRepository.saveAndFlush(Mockito.any())).thenReturn(account);

        User user = new User();
        user.setAccount(account);

        Mockito.when(userService.create(Mockito.any(User.class))).thenReturn(user);

        List<String> roles = List.of(UserRole.ROLE_CUSTOMER.name());

        RegisterResponse response = authService.register(request);
        assertEquals(account.getUsername(), response.getUsername());
        assertEquals(roles, response.getRoles());
    }

    @Test
    void shouldReturnRegisterSellerResponseWhenRegisterSellerSuccessfully() {
        RegisterSellerRequest request = new RegisterSellerRequest();
        request.setEmail("email@as.com");

        Mockito.doNothing().when(validation).validate(request);

        Role role = new Role();
        role.setId("3");
        role.setRole(UserRole.ROLE_SELLER);

        Mockito.when(roleService.getOrSave(UserRole.ROLE_SELLER)).thenReturn(role);

        Account account = new Account();
        account.setUsername("username");
        account.setPassword("password");
        account.setRoles(new ArrayList<>());

        Mockito.when(userServiceDetail.getByContext()).thenReturn(account);

        List<Role> roleList = account.getRoles();
        roleList.add(role);
        account.setRoles(roleList);

        User user = new User();
        user.setId("userId");
        user.setAccount(account);
        account.setUser(user);

        User userId = userService.getById(user.getId());
        String otp = otpService.generateOtp();


        Store store = Store.builder()
                .storeName(request.getStoreName())
                .address(request.getAddress())
                .email(request.getEmail())
                .user(userId)
                .otp(otp)
                .otpGenerateTime(LocalDateTime.now())
                .isVerified(false)
                .isActive(false)
                .build();

        storeService.createStore(store);

        RegisterSellerResponse response = authService.registerSeller(request);
        assertEquals(account.getUsername(), response.getUsername());
        assertEquals("email@as.com", response.getEmail());
        assertTrue(response.getRoles().contains(UserRole.ROLE_SELLER.name()));
    }

    @Test
    void shouldReturnLoginResponseWhenLogin() {
        LoginRequest request = new LoginRequest();
        request.setUsername("username");
        request.setPassword("password");

        Account account = new Account();
        account.setId("1");
        account.setIsEnable(true);
        account.setRoles(List.of(
                new Role("1", UserRole.ROLE_CUSTOMER)
        ));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(account);

        String token = "jwtToken";
        Mockito.when(jwtService.generateToken(account)).thenReturn(token);


        LoginResponse response = authService.login(request);
        assertEquals(token, response.getToken());

    }

    @Test
    void shouldReturnLoginSellerResponseWhenLoginSeller() {
        LoginRequest request = new LoginRequest();
        request.setUsername("username");
        request.setPassword("password");

        Store store = new Store();
        store.setId("1");
        store.setActive(true);

        User user = new User();
        user.setId("1");
        user.setStore(store);

        Account account = new Account();
        account.setId("1");
        account.setIsEnable(true);
        account.setRoles(List.of(
                new Role("1", UserRole.ROLE_SELLER)
        ));
        account.setUser(user);


        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(account);

        String token = "jwtToken";
        Mockito.when(jwtService.generateToken(account)).thenReturn(token);

        LoginSellerResponse response = authService.loginSeller(request);
        assertEquals(token, response.getToken());
    }

    @Test
    void shouldReturnStringWhenVerifyAccountSeller() {

    }

    @Test
    void regenerateOtp() {
    }

    @Test
    void shouldReturnTrueWhenValidateToken() {
        Account account = new Account();
        account.setId("1");
        account.setIsEnable(true);
        account.setRoles(List.of(
                new Role("1", UserRole.ROLE_CUSTOMER)
        ));

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Mockito.when(authentication.getPrincipal()).thenReturn(account);

        assertFalse(authService.validateToken());

    }
}