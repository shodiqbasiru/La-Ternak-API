package com.enigma.laternak.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartRequest {
    private String productId;
    private String userId;
    private Integer qty;
}
