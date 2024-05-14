package com.enigma.laternak.dto.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private String userId;
    private String storeId;
    private String username;
    private String token;
    private List<String> roles;
}
