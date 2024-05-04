package com.enigma.laternak.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponse {
    private String productName;
    private Double price;
    private Integer stock;
    private String description;
    private String storeId;
}
