package com.enigma.laternak.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private String id;
    private Integer qty;
    private String productId;
    private String userId;
}
