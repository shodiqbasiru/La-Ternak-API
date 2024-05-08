package com.enigma.laternak.service.impl;

import com.enigma.laternak.dto.request.PaginationUserRequest;
import com.enigma.laternak.dto.request.UpdateUserRequest;
import com.enigma.laternak.dto.response.AccountResponse;
import com.enigma.laternak.dto.response.StoreResponse;
import com.enigma.laternak.dto.response.UserResponse;
import com.enigma.laternak.entity.Account;
import com.enigma.laternak.entity.User;
import com.enigma.laternak.repository.UserRepository;
import com.enigma.laternak.service.UserService;
import com.enigma.laternak.service.UserServiceDetail;
import com.enigma.laternak.spesification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserServiceDetail userServiceDetail;

    @Override
    public User create(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public Page<UserResponse> getAll(PaginationUserRequest request) {
        if (request.getPage() <= 0) request.setPage(1);

        Specification<User> specification = UserSpecification.getSpecification(request);
        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);

        return userRepository.findAll(specification,pageable).map(user -> UserResponse.builder()
                .id(user.getId())
                .customerName(user.getCustomerName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .accountDetails(AccountResponse.builder()
                        .id(user.getAccount().getId())
                        .username(user.getAccount().getUsername())
                        .roles(user.getAccount().getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                        .build())
                .storeDetails(user.getStore() == null ? null : StoreResponse.builder()
                        .id(user.getStore().getId())
                        .storeName(user.getStore().getStoreName())
                        .email(user.getStore().getEmail())
                        .address(user.getStore().getAddress())
                        .build())
                .build());
    }

    @Override
    public User update(UpdateUserRequest user) {
        User currentUser = getById(user.getId());
        currentUser.setCustomerName(user.getCustomerName());
        currentUser.setPhoneNumber(user.getPhoneNumber());
        currentUser.setAddress(user.getAddress());
        return userRepository.saveAndFlush(currentUser);
    }

    @Override
    public void deleteAccountUser(String id) {
        User currentUser = getById(id);
        Account currentAccount = userServiceDetail.getUserById(currentUser.getId());
        currentAccount.setIsEnable(false);
    }

}
