package com.enigma.laternak.entity;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtClaims {
    private String accountId;
    private List<String> roles;
}
