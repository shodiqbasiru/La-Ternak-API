package com.enigma.laternak.repository;

import com.enigma.laternak.entity.Cart;
import com.enigma.laternak.entity.Product;
import com.enigma.laternak.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,String> {
    Cart findByUserAndProduct(User user, Product product);
    void deleteAllByUserId(String userId);
}
