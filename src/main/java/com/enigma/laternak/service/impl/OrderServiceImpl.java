package com.enigma.laternak.service.impl;

import com.enigma.laternak.dto.request.OrderRequest;
import com.enigma.laternak.dto.response.OrderDetailResponse;
import com.enigma.laternak.dto.response.OrderResponse;
import com.enigma.laternak.dto.response.PaymentResponse;
import com.enigma.laternak.entity.*;
import com.enigma.laternak.repository.OrderRepository;
import com.enigma.laternak.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailService orderDetailService;
    private final UserService userService;
    private final ProductService productService;
    private final PaymentService paymentService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        User user = userService.getById(request.getUserId());

        Order order = Order.builder()
                .orderDate(new Date())
                .user(user)
                .build();
        orderRepository.saveAndFlush(order);

        List<OrderDetail> orderDetails = request.getOrderDetailRequests().stream()
                .map(detailRequest -> {
                    Product product = productService.findById(detailRequest.getProductId());
                    if (product.getStock() - detailRequest.getQty() < 0) throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "Out of stock");

                    product.setStock(product.getStock() - detailRequest.getQty());

                    return OrderDetail.builder()
                            .product(product)
                            .order(order)
                            .qty(detailRequest.getQty())
                            .price(product.getPrice())
                            .build();
                }).toList();
        orderDetailService.createBulk(orderDetails);
        order.setOrderDetails(orderDetails);

        Payment payment = paymentService.createPayment(order);
        order.setPayment(payment);

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(payment.getId())
                .token(payment.getToken())
                .redirectUrl(payment.getRedirectUrl())
                .transactionStatus(payment.getTransactionStatus())
                .build();

        List<OrderDetailResponse> detailsResponse = orderDetails.stream()
                .map(detail -> OrderDetailResponse.builder()
                        .id(detail.getId())
                        .productId(detail.getProduct().getId())
                        .qty(detail.getQty())
                        .price(detail.getPrice())
                        .build())
                .toList();
        return OrderResponse.builder()
                .id(order.getId())
                .date(order.getOrderDate())
                .userId(order.getUser().getId())
                .orderDetails(detailsResponse)
                .paymentResponse(paymentResponse)
                .build();
    }
}
