package com.enigma.laternak.dto.request;

import com.enigma.laternak.dto.response.AccountResponse;
import lombok.*;

import java.util.List;

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
}
