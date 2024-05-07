package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.OrderRequest;
import com.enigma.laternak.dto.response.CommonResponse;
import com.enigma.laternak.dto.response.OrderResponse;
import com.enigma.laternak.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = ApiRoute.ORDER_API)
@Tag(name = "Order", description = "Api for order")
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Operation(
            summary = "Create Order",
            description = "Create a new order"
    )
    @SecurityRequirement(name = "Authorization")
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<OrderResponse>> createTransaction(@RequestBody OrderRequest request) {
        log.error("Request: {}", request.getUserId());
        OrderResponse order = orderService.createOrder(request);

        CommonResponse<OrderResponse> response = CommonResponse.<OrderResponse>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message("Order created successfully")
                .data(order)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
