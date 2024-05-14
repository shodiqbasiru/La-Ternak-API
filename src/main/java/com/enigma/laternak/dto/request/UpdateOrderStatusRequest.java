package com.enigma.laternak.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateOrderStatusRequest {
    private String orderId;
    private String transactionStatus;
}
