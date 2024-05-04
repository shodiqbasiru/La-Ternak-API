package com.enigma.laternak.security;

import com.enigma.wmb_api.dto.request.UpdateCustomerRequest;
import com.enigma.wmb_api.entity.Customer;
import com.enigma.wmb_api.entity.UserAccount;
import com.enigma.wmb_api.service.CustomerService;
import com.enigma.wmb_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component("authorizeSecurity")
@RequiredArgsConstructor
public class AuthorizationSecurity {
    private final CustomerService customerService;
    private final UserService userService;

    public boolean checkSameIdAsPrincipal(UpdateCustomerRequest request){
        Customer customer = customerService.getById(request.getId());
        UserAccount userAccount = userService.getByContext();

        if (!userAccount.getId().equals(customer.getUserAccount().getId())){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "cannot access this resource");

        }
        return true;
    }
}
