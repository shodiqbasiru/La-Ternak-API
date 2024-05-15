package com.enigma.laternak.service.impl;

import com.enigma.laternak.entity.Account;
import com.enigma.laternak.repository.AccountRepository;
import com.enigma.laternak.service.UserServiceDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceDetailImplTest {
    @Mock
    private AccountRepository accountRepository;
    private UserServiceDetail userServiceDetail;

    @BeforeEach
    void setUp() {
        userServiceDetail = new UserServiceDetailImpl(accountRepository);
    }

    @Test
    void shouldReturnAccountWhenGetUserById(){
        String id = "1";

        Account account = new Account();
        account.setId(id);

        Mockito.when(accountRepository.findById(id)).thenReturn(Optional.of(account));

        Account result = userServiceDetail.getUserById(id);

        assertEquals(account, result);
    }

    @Test
    void shouldReturnAccountWhenGetByContext(){
        Account account = new Account();
        account.setId("1");
        account.setUsername("username");

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);

        Mockito.when(authentication.getPrincipal()).thenReturn(account.getUsername());
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Mockito.when(accountRepository.findByUsername(account.getUsername())).thenReturn(Optional.of(account));

        Account result = userServiceDetail.getByContext();

        assertEquals(account, result);
    }

    @Test
    void shouldReturnUserDetailsWhenLoadUserByUsername(){
        String username = "username";

        Account account = new Account();
        account.setUsername(username);

        Mockito.when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));

        UserDetails result = userServiceDetail.loadUserByUsername(username);

        assertEquals(account, result);
    }
}