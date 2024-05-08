package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.dto.request.OrderRequest;
import com.enigma.laternak.dto.request.PaginationOrderRequest;
import com.enigma.laternak.dto.response.CommonResponse;
import com.enigma.laternak.dto.response.OrderResponse;
import com.enigma.laternak.dto.response.PagingResponse;
import com.enigma.laternak.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(
            summary = "Get All Order",
            description = "Get all order"
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<OrderResponse>>> getAllOrder(
            @RequestParam(name = "page",defaultValue = "1") Integer page,
            @RequestParam(name = "size",defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy",defaultValue = "orderDate") String sortBy,
            @RequestParam(name = "direction",defaultValue = "asc") String direction,
            @RequestParam(name = "startDate",required = false) String startDate,
            @RequestParam(name = "endDate",required = false) String endDate
    ) {
        PaginationOrderRequest request = PaginationOrderRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        Page<OrderResponse> order = orderService.getAllOrder(request);

        PagingResponse paging = PagingResponse.builder()
                .page(order.getPageable().getPageNumber() + 1)
                .size(order.getPageable().getPageSize())
                .totalPages(order.getTotalPages())
                .totalElement(order.getTotalElements())
                .hasNext(order.hasNext())
                .hasPrevious(order.hasPrevious())
                .build();

        CommonResponse<List<OrderResponse>> response = CommonResponse.<List<OrderResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Get all order successfully")
                .data(order.getContent())
                .pagingResponse(paging)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
