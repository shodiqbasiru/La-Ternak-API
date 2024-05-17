package com.enigma.laternak.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchProductRequest {
    private Integer size;
    private Integer page;
    private String sortBy;
    private String direction;
    private String productName;
    private Integer price;
    private Integer stock;
    private String description;
}
