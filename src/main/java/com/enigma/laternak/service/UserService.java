package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.PaginationUserRequest;
import com.enigma.laternak.dto.request.UpdateUserRequest;
import com.enigma.laternak.dto.response.UserResponse;
import com.enigma.laternak.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    User create(User user);

    User getById(String id);

    UserResponse getUserById(String id);

    Page<UserResponse> getAll(PaginationUserRequest request);

    User update(UpdateUserRequest user);

    void deleteAccountUser(String id);
}
