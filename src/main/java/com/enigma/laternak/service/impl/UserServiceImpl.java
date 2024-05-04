package com.enigma.laternak.service.impl;

import com.enigma.laternak.entity.User;
import com.enigma.laternak.repository.UserRepository;
import com.enigma.laternak.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public User create(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }
}
