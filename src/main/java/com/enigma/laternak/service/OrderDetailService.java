package com.enigma.laternak.service;

import com.enigma.laternak.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService {
    List<OrderDetail> createBulk(List<OrderDetail> orderDetails);
}
