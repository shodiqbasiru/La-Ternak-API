package com.enigma.laternak.dto.response;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterSellerResponse {
    private String username;
    private String email;
    private String storeName;;
    private boolean isVerified;
    private List<String> roles;
}
