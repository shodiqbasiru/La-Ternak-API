package com.enigma.laternak.service.impl;

import com.enigma.laternak.entity.OrderDetail;
import com.enigma.laternak.repository.OrderDetailRepository;
import com.enigma.laternak.service.OrderDetailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OrderDetailServiceImplTest {

    @Mock
    private OrderDetailRepository orderDetailRepository;
    private OrderDetailService orderDetailService;

    @BeforeEach
    void setUp() {
        orderDetailService = new OrderDetailServiceImpl(orderDetailRepository);
    }

    @Test
    void shouldReturnListOrderDetailWhenCreateBulk() {
        List<OrderDetail> orderDetails = new ArrayList<>();
        OrderDetail orderDetail = OrderDetail.builder()
                .id("1")
                .build();
        orderDetails.add(orderDetail);

        when(orderDetailRepository.saveAllAndFlush(orderDetails)).thenReturn(orderDetails);
        List<OrderDetail> actual = orderDetailService.createBulk(orderDetails);

          assertEquals(orderDetails, actual);
    }
}