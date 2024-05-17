package com.enigma.laternak.security;

import com.enigma.laternak.dto.request.UpdateUserRequest;
import com.enigma.laternak.entity.Account;
import com.enigma.laternak.entity.User;
import com.enigma.laternak.service.UserService;
import com.enigma.laternak.service.UserServiceDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component("authorizeSecurity")
@RequiredArgsConstructor
public class AuthorizationSecurity {
    private final UserService userService;
    private final UserServiceDetail userServiceDetail;

    public boolean checkSameIdAsPrincipal(UpdateUserRequest request){
        User user = userService.getById(request.getId());
        Account userAccount = userServiceDetail.getByContext();

        if (!userAccount.getId().equals(user.getAccount().getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "cannot access this resource");

        }
        return true;
    }
}
