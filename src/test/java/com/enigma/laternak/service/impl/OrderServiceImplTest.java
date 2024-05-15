package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.OrderStatus;
import com.enigma.laternak.constant.TransactionStatus;
import com.enigma.laternak.dto.request.*;
import com.enigma.laternak.dto.response.OrderDetailResponse;
import com.enigma.laternak.dto.response.OrderResponse;
import com.enigma.laternak.dto.response.PaymentResponse;
import com.enigma.laternak.entity.*;
import com.enigma.laternak.repository.OrderRepository;
import com.enigma.laternak.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailService orderDetailService;
    @Mock
    private UserService userService;
    @Mock
    private ProductService productService;
    @Mock
    private PaymentService paymentService;
    @Mock
    private CartService cartService;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, orderDetailService, userService, productService, paymentService, cartService);
    }

    @Test
    void shouldReturnOrderResponseWhenCreateOrder() {
        OrderRequest request = new OrderRequest();
        request.setUserId("1");
        request.setOrderDetailRequests(
                List.of(
                        OrderDetailRequest.builder()
                                .productId("1")
                                .qty(1)
                                .build()
                )
        );

        User user = new User();
        user.setId("1");

        Mockito.when(userService.getById("1")).thenReturn(user);

        Order order = new Order();
        order.setId("1");
        order.setUser(user);
        order.setOrderStatus(OrderStatus.UNPAID);


        Product product = new Product();
        product.setId("1");

        Mockito.when(orderRepository.saveAndFlush(ArgumentMatchers.any(Order.class))).thenReturn(order);

        List<OrderDetail> orderDetails = request.getOrderDetailRequests().stream()
                .map(detailRequest -> {
                    Mockito.when(productService.findById(detailRequest.getProductId())).thenReturn(product);

                    product.setStock(1);

                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setQty(detailRequest.getQty());
                    orderDetail.setProduct(product);
                    orderDetail.setPrice(product.getPrice());
                    orderDetail.setOrder(order);
                    return orderDetail;
                }).toList();

        Mockito.when(orderDetailService.createBulk(ArgumentMatchers.anyList())).thenReturn(orderDetails);
        order.setOrderDetails(orderDetails);

        Payment payment = new Payment();
        payment.setId("1");
        payment.setTransactionStatus(TransactionStatus.ORDERED);

        Mockito.when(paymentService.createPayment(ArgumentMatchers.any(Order.class))).thenReturn(payment);
        order.setPayment(payment);

        Mockito.doNothing().when(cartService).deleteCart("1");

        OrderResponse expected = new OrderResponse();
        expected.setId("1");
        expected.setUserId("1");
        expected.setOrderStatus(OrderStatus.UNPAID.getDescription());
        expected.setOrderDetails(
                List.of(
                        OrderDetailResponse.builder()
                                .productId("1")
                                .qty(1)
                                .price(1)
                                .build()
                )
        );
        expected.setPaymentResponse(
                PaymentResponse.builder()
                        .id("1")
                        .transactionStatus(payment.getTransactionStatus().getDescription())
                        .token(payment.getToken())
                        .redirectUrl(payment.getRedirectUrl())
                        .build()
        );

        OrderResponse actual = orderService.createOrder(request);

        assertEquals(expected.getUserId(), actual.getUserId());
    }

    @Test
    void shouldReturnPageResponseWhenGetAllOrder() {
        PaginationOrderRequest request = new PaginationOrderRequest();
        request.setPage(1);
        request.setSize(1);
        request.setDirection("ASC");
        request.setSortBy("id");

        Order order = new Order();
        order.setId("1");
        order.setOrderStatus(OrderStatus.UNPAID);
        order.setOrderDetails(
                List.of(
                        OrderDetail.builder()
                                .id("1")
                                .product(Product.builder().id("1").build())
                                .qty(1)
                                .price(1)
                                .build()
                )
        );
        order.setPayment(Payment.builder()
                .id("1")
                .transactionStatus(TransactionStatus.ORDERED)
                .token("token")
                .redirectUrl("redirectUrl")
                .build());
        order.setUser(new User());

        Page<Order> page = new PageImpl<>(List.of(order));

        Mockito.when(orderRepository.findAll(Mockito.any(Specification.class), Mockito.any(Pageable.class))).thenReturn(page);

        List<OrderDetailResponse> detailResponses = order.getOrderDetails().stream()
                .map(detail -> OrderDetailResponse.builder()
                        .id(detail.getId())
                        .productId(detail.getProduct().getId())
                        .qty(detail.getQty())
                        .price(detail.getPrice())
                        .build())
                .toList();
        PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(order.getPayment().getId())
                .transactionStatus(order.getPayment().getTransactionStatus().getDescription())
                .token(order.getPayment().getToken())
                .redirectUrl(order.getPayment().getRedirectUrl())
                .build();

        OrderResponse expected = OrderResponse.builder()
                .id(order.getId())
                .orderStatus(order.getOrderStatus().getDescription())
                .orderDetails(detailResponses)
                .paymentResponse(paymentResponse)
                .build();

        Page<OrderResponse> result = orderService.getAllOrder(request);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());


    }

    @Test
    void shouldReturnListOrderResponseWhenGetAllOrder() {
        OrderSpecificationRequest request = new OrderSpecificationRequest();
        request.setOrderStatus(OrderStatus.UNPAID.getDescription());

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setId("1");
        orderDetail.setProduct(new Product());

        Order order = new Order();
        order.setId("1");
        order.setOrderStatus(OrderStatus.UNPAID);
        order.setOrderDetails(
                List.of(orderDetail)
        );

        Payment payment = new Payment();
        payment.setId("1");
        payment.setTransactionStatus(TransactionStatus.ORDERED);
        payment.setOrder(order);

        order.setPayment(payment);
        order.setUser(new User());
        orderDetail.setOrder(order);

        List<Order> orders = List.of(order);

        Mockito.when(orderRepository.findAll(Mockito.any(Specification.class))).thenReturn(orders);


        List<OrderResponse> result = orderService.getAllOrder(request);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void shouldUpdateStatusOrder() {
        UpdateOrderStatusRequest request = new UpdateOrderStatusRequest();
        request.setOrderId("1");
        request.setTransactionStatus(TransactionStatus.SETTLEMENT.getDescription());

        Order order = new Order();
        order.setId("1");
        order.setOrderStatus(OrderStatus.UNPAID);
        order.setPayment(Payment.builder()
                .id("1")
                .transactionStatus(TransactionStatus.ORDERED)
                .build());

        Mockito.when(orderRepository.findById("1")).thenReturn(java.util.Optional.of(order));

        orderService.updateStatus(request);

        assertEquals(TransactionStatus.SETTLEMENT, order.getPayment().getTransactionStatus());
        assertEquals(OrderStatus.PACKED, order.getOrderStatus());
    }

    @Test
    void shouldChangeStatusOrder() {
        Order order = new Order();
        order.setId("1");
        order.setOrderStatus(OrderStatus.PACKED);

        Mockito.when(orderRepository.findById("1")).thenReturn(Optional.of(order));

        orderService.changeStatusOrder("1");

        assertEquals(OrderStatus.SEND, order.getOrderStatus());
    }

    @Test
    void shouldChangeStatusFromSendToReceived() {
        Order order = new Order();
        order.setId("1");
        order.setOrderStatus(OrderStatus.SEND);

        Mockito.when(orderRepository.findById("1")).thenReturn(Optional.of(order));

        orderService.changeStatusOrder("1");

        assertEquals(OrderStatus.RECEIVED, order.getOrderStatus());
    }

    @Test
    void shouldReturnBadRequestWhenChangeStatusOrder() {
        Order order = new Order();
        order.setId("1");
        order.setOrderStatus(OrderStatus.RECEIVED);

        Mockito.when(orderRepository.findById("1")).thenReturn(Optional.of(order));

        assertThrows(ResponseStatusException.class, () -> orderService.changeStatusOrder("1"));
    }
}