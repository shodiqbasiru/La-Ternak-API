package com.enigma.laternak.repository;


import com.enigma.laternak.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String>{
}
