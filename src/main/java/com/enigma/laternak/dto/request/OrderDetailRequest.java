package com.enigma.laternak.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailRequest {
    private Integer qty;
    private String productId;
}
