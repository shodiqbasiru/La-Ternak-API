package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.Message;
import com.enigma.laternak.entity.Account;
import com.enigma.laternak.repository.AccountRepository;
import com.enigma.laternak.service.UserServiceDetail;
import com.enigma.laternak.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceDetailImpl implements UserServiceDetail {
    private final AccountRepository accountRepository;

    @Override
    public Account getUserById(String id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_USER_NOT_FOUND));
    }

    @Override
    public Account getByContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return accountRepository.findByUsername(authentication.getPrincipal().toString())
                .orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_USER_NOT_FOUND));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_USER_NOT_FOUND));
    }
}
