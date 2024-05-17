package com.enigma.laternak.service.impl;

import com.enigma.laternak.entity.OrderDetail;
import com.enigma.laternak.repository.OrderDetailRepository;
import com.enigma.laternak.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<OrderDetail> createBulk(List<OrderDetail> orderDetails) {
        return orderDetailRepository.saveAllAndFlush(orderDetails);
    }
}
