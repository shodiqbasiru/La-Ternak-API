package com.enigma.laternak.service;

import com.enigma.laternak.dto.request.OrderRequest;
import com.enigma.laternak.dto.request.OrderSpecificationRequest;
import com.enigma.laternak.dto.request.PaginationOrderRequest;
import com.enigma.laternak.dto.request.UpdateOrderStatusRequest;
import com.enigma.laternak.dto.response.OrderResponse;
import com.enigma.laternak.entity.Order;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    OrderResponse createOrder(OrderRequest request);
    Order getById(String id);
    OrderResponse getOrderById(String id);
    Page<OrderResponse> getAllOrder(PaginationOrderRequest request);
    List<OrderResponse> getAllOrder(OrderSpecificationRequest request);
    void updateStatus(UpdateOrderStatusRequest request);
    void changeStatusOrder(String id);
}
