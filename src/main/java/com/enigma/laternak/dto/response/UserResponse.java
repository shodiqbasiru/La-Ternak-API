package com.enigma.laternak.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String id;
    private String customerName;
    private String phoneNumber;
    private String address;
    private AccountResponse accountDetails;
    private StoreResponse storeDetails;
}
