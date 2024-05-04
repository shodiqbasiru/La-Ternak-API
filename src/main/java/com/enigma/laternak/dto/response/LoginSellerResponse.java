package com.enigma.laternak.dto.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginSellerResponse {
    private String username;
    private String email;
    private String token;
    private List<String> role;
}
