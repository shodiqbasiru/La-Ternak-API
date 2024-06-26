package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.Message;
import com.enigma.laternak.constant.OrderStatus;
import com.enigma.laternak.constant.TransactionStatus;
import com.enigma.laternak.dto.request.OrderRequest;
import com.enigma.laternak.dto.request.OrderSpecificationRequest;
import com.enigma.laternak.dto.request.PaginationOrderRequest;
import com.enigma.laternak.dto.request.UpdateOrderStatusRequest;
import com.enigma.laternak.dto.response.OrderDetailResponse;
import com.enigma.laternak.dto.response.OrderResponse;
import com.enigma.laternak.dto.response.PaymentResponse;
import com.enigma.laternak.entity.*;
import com.enigma.laternak.repository.OrderRepository;
import com.enigma.laternak.service.*;
import com.enigma.laternak.spesification.OrderSpecification;
import com.enigma.laternak.util.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    private final CartService cartService;
    private final UserServiceDetail userServiceDetail;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public OrderResponse createOrder(OrderRequest request) {
        User user = userService.getById(request.getUserId());

        Order order = Order.builder()
                .orderDate(new Date())
                .user(user)
                .orderStatus(OrderStatus.UNPAID)
                .build();
        orderRepository.saveAndFlush(order);

        List<OrderDetail> orderDetails = request.getOrderDetailRequests().stream()
                .map(detailRequest -> {
                    Product product = productService.findById(detailRequest.getProductId());
                    if (product.getStock() - detailRequest.getQty() < 0)
                        throw ResponseMessage.error(HttpStatus.NOT_ACCEPTABLE, Message.ERROR_OUT_OF_STOCK);

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

        cartService.deleteCart(request.getUserId());

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(payment.getId())
                .token(payment.getToken())
                .redirectUrl(payment.getRedirectUrl())
                .transactionStatus(payment.getTransactionStatus().getDescription())
                .build();

        List<OrderDetailResponse> detailsResponse = orderDetails.stream()
                .map(detail -> OrderDetailResponse.builder()
                        .id(detail.getId())
                        .productId(detail.getProduct().getId())
                        .productName(detail.getProduct().getProductName())
                        .qty(detail.getQty())
                        .price(detail.getPrice())
                        .build())
                .toList();
        return OrderResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .userId(order.getUser().getId())
                .customerName(order.getUser().getCustomerName())
                .address(order.getUser().getAddress())
                .orderStatus(order.getOrderStatus().getDescription())
                .orderDetails(detailsResponse)
                .paymentResponse(paymentResponse)
                .build();
    }

    @Override
    public Order getById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_ORDER_NOT_FOUND));
    }

    @Override
    public OrderResponse getOrderById(String id) {
        Order order = getById(id);
        return getOrderResponse(order);
    }

    @Override
    public Page<OrderResponse> getAllOrder(PaginationOrderRequest request) {
        if (request.getPage() < 0) request.setPage(1);

        Sort sort = Sort.by(Sort.Direction.fromString(request.getDirection()), request.getSortBy());
        Pageable pageable = PageRequest.of((request.getPage() - 1), request.getSize(), sort);
        Specification<Order> specification = OrderSpecification.getSpecification(request);

        Page<Order> orderPage = orderRepository.findAll(specification, pageable);
        return orderPage.map(OrderServiceImpl::getOrderResponse);
    }

    @Override
    public List<OrderResponse> getAllOrder(OrderSpecificationRequest request) {
        String accountId = userServiceDetail.getByContext().getId();
        Specification<Order> specification = OrderSpecification.getSpecification(request);
        List<Order> orders = orderRepository.findAll(specification);
        return orders.stream()
                .filter(order -> order.getUser().getAccount().getId().equals(accountId))
                .map(OrderServiceImpl::getOrderResponse).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateStatus(UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_ORDER_NOT_FOUND));
        Payment payment = order.getPayment();
        payment.setTransactionStatus(TransactionStatus.getByName(request.getTransactionStatus()));
        if (payment.getTransactionStatus() == TransactionStatus.SETTLEMENT) {
            order.setOrderStatus(OrderStatus.PACKED);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void changeStatusOrder(String id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> ResponseMessage.error(HttpStatus.NOT_FOUND, Message.ERROR_ORDER_NOT_FOUND));
        if (order.getOrderStatus() == OrderStatus.PACKED) {
            order.setOrderStatus(OrderStatus.SEND);
        } else if (order.getOrderStatus() == OrderStatus.SEND) {
            order.setOrderStatus(OrderStatus.RECEIVED);
        } else {
            throw ResponseMessage.error(HttpStatus.BAD_REQUEST, "Invalid order status");
        }
    }

    private static OrderResponse getOrderResponse(Order order) {
        List<OrderDetailResponse> detailsResponse = order.getOrderDetails().stream()
                .map(detail -> OrderDetailResponse.builder()
                        .id(detail.getId())
                        .productId(detail.getProduct().getId())
                        .productName(detail.getProduct().getProductName())
                        .qty(detail.getQty())
                        .price(detail.getPrice())
                        .build())
                .toList();

        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(order.getPayment().getId())
                .token(order.getPayment().getToken())
                .redirectUrl(order.getPayment().getRedirectUrl())
                .transactionStatus(order.getPayment().getTransactionStatus().getDescription())
                .build();

        return OrderResponse.builder()
                .id(order.getId())
                .orderDate(order.getOrderDate())
                .userId(order.getUser().getId())
                .customerName(order.getUser().getCustomerName())
                .orderStatus(order.getOrderStatus().getDescription() )
                .orderDetails(detailsResponse)
                .address(order.getUser().getAddress())
                .paymentResponse(paymentResponse)
                .build();
    }
}
