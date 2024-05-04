package com.enigma.laternak.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterSellerRequest {
    private String storeName;
    private String email;
    private String address;
}
