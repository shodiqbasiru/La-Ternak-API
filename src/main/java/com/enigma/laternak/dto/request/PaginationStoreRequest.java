package com.enigma.laternak.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationStoreRequest {
    private Integer size;
    private Integer page;
    private String sortBy;
    private String direction;
    private String storeName;
}
