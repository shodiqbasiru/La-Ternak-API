package com.enigma.laternak.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductSpecificationRequest {
    private String productName;
    private Integer maxPrice;
    private Integer minPrice;
    private String sortBy;
    private String sortDirection;
}
