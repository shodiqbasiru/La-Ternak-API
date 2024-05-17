package com.enigma.laternak.dto.request;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSpecificationRequest {
    private String orderStatus;
}
