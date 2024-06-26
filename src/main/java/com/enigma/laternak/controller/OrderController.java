package com.enigma.laternak.controller;

import com.enigma.laternak.constant.ApiRoute;
import com.enigma.laternak.constant.Message;
import com.enigma.laternak.dto.request.OrderRequest;
import com.enigma.laternak.dto.request.OrderSpecificationRequest;
import com.enigma.laternak.dto.request.PaginationOrderRequest;
import com.enigma.laternak.dto.request.UpdateOrderStatusRequest;
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
import java.util.Map;

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
                .message(Message.SUCCESS_CREATE_ORDER.getMessage())
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
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "orderDate") String sortBy,
            @RequestParam(name = "direction", defaultValue = "asc") String direction,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate,
            @RequestParam(name = "orderStatus", required = false) String orderStatus,
            @RequestParam(name = "storeId", required = false) String storeId
    ) {
        PaginationOrderRequest request = PaginationOrderRequest.builder()
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .direction(direction)
                .startDate(startDate)
                .endDate(endDate)
                .orderStatus(orderStatus)
                .storeId(storeId)
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

    @Operation(
            summary = "Get All Order",
            description = "Get All Order Without Pagination"
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping(path = "/mobile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommonResponse<List<OrderResponse>>> getAllOrder(
            @RequestParam(name = "orderStatus", required = false) String orderStatus
    ) {
        OrderSpecificationRequest request = OrderSpecificationRequest.builder()
                .orderStatus(orderStatus)
                .build();
        List<OrderResponse> order = orderService.getAllOrder(request);

        CommonResponse<List<OrderResponse>> response = CommonResponse.<List<OrderResponse>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(Message.SUCCESS_GET_ALL_DATA.getMessage())
                .data(order)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "Get Order",
            description = "Get order by id"
    )
    @SecurityRequirement(name = "Authorization")
    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<OrderResponse>> getOrderById(@PathVariable String id) {
        OrderResponse order = orderService.getOrderById(id);
        CommonResponse<OrderResponse> response = CommonResponse.<OrderResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(Message.SUCCESS_GET_DATA.getMessage())
                .data(order)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/status")
    public ResponseEntity<CommonResponse<?>> updateStatus(@RequestBody Map<String, Object> request) {
        UpdateOrderStatusRequest updateTransactionStatusRequest = UpdateOrderStatusRequest.builder()
                .orderId(request.get("order_id").toString())
                .transactionStatus(request.get("transaction_status").toString())
                .build();
        orderService.updateStatus(updateTransactionStatusRequest);
        return ResponseEntity.ok(CommonResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .message(Message.SUCCESS_UPDATE.getMessage())
                .build());
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping(
            path = "/order-status/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<CommonResponse<OrderResponse>> updateOrderStatus(@PathVariable String id) {
        orderService.changeStatusOrder(id);
        CommonResponse<OrderResponse> response = CommonResponse.<OrderResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message(Message.SUCCESS_UPDATE_ORDER_STATUS.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
