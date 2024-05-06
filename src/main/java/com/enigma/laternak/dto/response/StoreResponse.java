package com.enigma.laternak.dto.response;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreResponse {
    private String id;
    private String storeName;
    private String email;
    private String address;
    private List<ProductResponse> productDetails;
    private boolean isActive;
}
