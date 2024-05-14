package com.enigma.laternak.service.impl;

import com.enigma.laternak.constant.OrderStatus;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final RestClient restClient;
    private final String SECRET_KEY;
    private final String SNAP_BASE_URL;

    @Autowired
    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            RestClient restClient,
            @Value("${midtrans.api.key}") String secretKey,
            @Value("${midtrans.api.snap-url}") String snapBaseUrl
    ) {
        this.paymentRepository = paymentRepository;
        this.restClient = restClient;
        SECRET_KEY = secretKey;
        SNAP_BASE_URL = snapBaseUrl;
    }

    @Override
    public Payment createPayment(Order order) {
        int amount = order.getOrderDetails().stream()
                .mapToInt(value -> (value.getQty() * value.getPrice()))
                .reduce(0, Integer::sum);

        PaymentDetailRequest paymentDetailRequest = PaymentDetailRequest.builder()
                .orderId(order.getId())
                .amount(amount)
                .build();

        List<PaymentItemDetailRequest> itemDetailRequests = order.getOrderDetails().stream()
                .map(orderDetail -> PaymentItemDetailRequest.builder()
                        .name(orderDetail.getProduct().getProductName())
                        .price(orderDetail.getPrice())
                        .quantity(orderDetail.getQty())
                        .build())
                .toList();

        List<String> paymentMethods = List.of("shopeepay", "gopay");

        PaymentRequest request = PaymentRequest.builder()
                .paymentDetail(paymentDetailRequest)
                .paymentItemDetails(itemDetailRequests)
                .paymentMethod(paymentMethods)
                .build();

        ResponseEntity<Map<String, String>> response = restClient.post()
                .uri(SNAP_BASE_URL)
                .body(request)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + SECRET_KEY)
                .retrieve().toEntity(new ParameterizedTypeReference<>() {
                });
        Map<String, String> body = response.getBody();

        Payment payment = Payment.builder()
                .token(body.get("token"))
                .redirectUrl(body.get("redirect_url"))
                .transactionStatus(TransactionStatus.ORDERED)
                .build();
        paymentRepository.saveAndFlush(payment);
        return payment;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void checkFailedAndUpdateStatus() {
        List<TransactionStatus> transactionStatus = List.of(
                TransactionStatus.DENY,
                TransactionStatus.CANCEL,
                TransactionStatus.EXPIRE,
                TransactionStatus.FAILURE
        );
        List<Payment> payments = paymentRepository.findAllByTransactionStatusIn(transactionStatus);

        for (Payment payment : payments) {
            for (OrderDetail orderDetail : payment.getOrder().getOrderDetails()) {
                Product product = orderDetail.getProduct();
                product.setStock(product.getStock() + orderDetail.getQty());
            }
            payment.setTransactionStatus(TransactionStatus.FAILURE);
        }
    }
}
