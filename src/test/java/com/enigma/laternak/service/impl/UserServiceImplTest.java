package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.UserRole;
import com.enigma.laternak.dto.request.PaginationUserRequest;
import com.enigma.laternak.dto.request.UpdateUserRequest;
import com.enigma.laternak.dto.request.UserRequest;
import com.enigma.laternak.dto.response.UserResponse;
import com.enigma.laternak.entity.*;
import com.enigma.laternak.repository.UserRepository;
import com.enigma.laternak.service.UserService;
import com.enigma.laternak.service.UserServiceDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserServiceDetail userServiceDetail;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, userServiceDetail);
    }

    @Test
    void shouldReturnUserWhenCreate() {
        User user = new User();
        user.setId("1");

        Mockito.when(userRepository.saveAndFlush(user)).thenReturn(user);

        User result = userService.create(user);

        assertEquals(user, result);
    }

    @Test
    void shouldReturnUserWhenGetById() {
        User user = new User();
        user.setId("1");

        Mockito.when(userRepository.findById("1")).thenReturn(java.util.Optional.of(user));

        User result = userService.getById("1");

        assertEquals(user, result);
    }

    @Test
    void shouldReturnPageUserResponseWhenGetAll() {
        PaginationUserRequest request = new PaginationUserRequest();
        request.setPage(1);
        request.setSize(1);
        request.setDirection("ASC");
        request.setSortBy("id");

        Role role = new Role();
        role.setId("1");
        role.setRole(UserRole.ROLE_CUSTOMER);

        Account account = new Account();
        account.setId("1");
        account.setRoles(List.of(role));

        User user = new User();
        user.setId("1");
        user.setAccount(account);
        user.setStore(new Store());

        Page<User> page = new PageImpl<>(List.of(user));

        Mockito.when(userRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(page);

        Page<UserResponse> result = userService.getAll(request);

        assertEquals(1, result.getTotalElements());

    }

    @Test
    void shouldReturnUserWhenUpdate() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setCustomerName("name");
        request.setAddress("address");

        ImageUser imageUser = new ImageUser();
        imageUser.setId("1");

        User user = new User();
        user.setId("1");
        user.setCustomerName(request.getCustomerName());
        user.setImageUser(imageUser);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.saveAndFlush(user)).thenReturn(user);

        User result = userService.update(request);

        assertEquals(user, result);
    }

    @Test
    void shouldReturnVoidWhenDeleteAccountUser() {
        User user = new User();
        user.setId("1");

        Account account = new Account();
        account.setId("1");
        account.setIsEnable(true);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(user));
        Mockito.when(userServiceDetail.getUserById(Mockito.any())).thenReturn(account);

        userService.deleteAccountUser("1");

        assertFalse(account.getIsEnable());

    }
}