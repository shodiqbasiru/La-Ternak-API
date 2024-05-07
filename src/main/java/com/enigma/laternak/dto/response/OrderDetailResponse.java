package com.enigma.laternak.dto.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetailResponse {
    private String id;
    private Integer price;
    private Integer qty;
    private String productId;
}
