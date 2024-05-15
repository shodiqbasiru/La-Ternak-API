package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.TransactionStatus;
import com.enigma.laternak.dto.request.PaymentDetailRequest;
import com.enigma.laternak.dto.request.PaymentItemDetailRequest;
import com.enigma.laternak.dto.request.PaymentRequest;
import com.enigma.laternak.entity.Order;
import com.enigma.laternak.entity.OrderDetail;
import com.enigma.laternak.entity.Payment;
import com.enigma.laternak.entity.Product;
import com.enigma.laternak.repository.PaymentRepository;
import com.enigma.laternak.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RestClient restClient;

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentServiceImpl(paymentRepository, restClient, "SECRET_KEY", "SNAP_BASE_URL");
    }

    @Test
    void shouldReturnPaymentWhenCreatePayment() {
        Order order = new Order();
        order.setId("1");
        Product product = new Product();
        product.setProductName("Product 1");
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setProduct(product);
        orderDetail.setQty(1);
        orderDetail.setPrice(1000);
        order.setOrderDetails(List.of(orderDetail));

        PaymentDetailRequest paymentDetailRequest = PaymentDetailRequest.builder()
                .orderId(order.getId())
                .amount(1000)
                .build();

        PaymentItemDetailRequest itemDetailRequest = PaymentItemDetailRequest.builder()
                .name("Product 1")
                .price(1000)
                .build();

        PaymentRequest paymentRequest = PaymentRequest.builder()
                .paymentDetail(paymentDetailRequest)
                .paymentItemDetails(List.of(itemDetailRequest))
                .build();

        ResponseEntity<Map<String, String>> responseEntity = new ResponseEntity<>(Map.of("token", "1", "redirect_url", "http://localhost:8080", "transaction_status", TransactionStatus.ORDERED.name()), HttpStatus.OK);

        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.AUTHORIZATION, "Basic U0VOQ1JFVF9LRVg6");

        RestClient.RequestBodyUriSpec requestBodyUriSpecMock = Mockito.mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpecMock = Mockito.mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpecMock = Mockito.mock(RestClient.ResponseSpec.class);

        Mockito.when(restClient.post()).thenReturn(requestBodyUriSpecMock);
        Mockito.when(requestBodyUriSpecMock.uri(Mockito.anyString())).thenReturn(requestBodySpecMock);
        Mockito.when(requestBodySpecMock.body(Mockito.any())).thenReturn(requestBodySpecMock);
        Mockito.when(requestBodySpecMock.header(Mockito.anyString(), Mockito.anyString())).thenReturn(requestBodySpecMock);
        Mockito.when(requestBodySpecMock.retrieve()).thenReturn(responseSpecMock);
        Mockito.when(responseSpecMock.toEntity(Mockito.any(ParameterizedTypeReference.class))).thenReturn(responseEntity);

        Payment payment = Payment.builder()
                .token("1")
                .redirectUrl("http://localhost:8080")
                .transactionStatus(TransactionStatus.ORDERED)
                .build();

        Mockito.when(paymentRepository.saveAndFlush(payment))
                .thenReturn(payment);

        Payment result = paymentService.createPayment(order);

        assertEquals(payment, result);
    }

    @Test
    void shouldUpdateStatusWhenCheckFailed() {
        Payment payment = new Payment();
        payment.setTransactionStatus(TransactionStatus.DENY);

        Order order = new Order();
        OrderDetail orderDetail = new OrderDetail();
        Product product = new Product();
        product.setProductName("Product 1");
        product.setStock(10);
        orderDetail.setProduct(product);
        orderDetail.setQty(1);
        orderDetail.setPrice(1000);
        order.setOrderDetails(List.of(orderDetail));
        payment.setOrder(order);

        when(paymentRepository.findAllByTransactionStatusIn(List.of(
                TransactionStatus.DENY,
                TransactionStatus.CANCEL,
                TransactionStatus.EXPIRE,
                TransactionStatus.FAILURE
        ))).thenReturn(List.of(payment));

        paymentService.checkFailedAndUpdateStatus();

        assertEquals(TransactionStatus.FAILURE, payment.getTransactionStatus());
    }
}