package com.enigma.laternak.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private String id;
    private String productName;
    private Integer price;
    private Integer stock;
    private String description;
    private String storeId;
    private List<ReviewResponse> reviews;
}
