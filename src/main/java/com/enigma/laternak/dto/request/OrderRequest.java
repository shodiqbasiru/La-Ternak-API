package com.enigma.laternak.dto.request;

import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private String userId;
    private List<OrderDetailRequest> orderDetailRequests;
}
