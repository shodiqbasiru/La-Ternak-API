package com.enigma.laternak.dto.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private String id;
    private String token;
    private String redirectUrl;
    private String transactionStatus;
}
