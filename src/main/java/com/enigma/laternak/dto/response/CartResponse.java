package com.enigma.laternak.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {
    private String id;
    private Integer qty;
    private String productId;
    private String productName;
    private Integer price;
    private List<ImageProductResponse> images;
    private String userId;
}
