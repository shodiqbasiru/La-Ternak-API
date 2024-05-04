package com.enigma.laternak.service;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginSellerRequest {
    private String email;
    private String password;
}
