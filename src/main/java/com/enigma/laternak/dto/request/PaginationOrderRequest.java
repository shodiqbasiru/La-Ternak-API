package com.enigma.laternak.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginationOrderRequest {
    private Integer page;
    private Integer size;
    private String sortBy;
    private String direction;
    private String startDate;
    private String endDate;
}
