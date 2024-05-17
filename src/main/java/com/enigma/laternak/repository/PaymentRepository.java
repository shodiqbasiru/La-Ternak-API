package com.enigma.laternak.repository;

import com.enigma.laternak.constant.TransactionStatus;
import com.enigma.laternak.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,String> {
    List<Payment> findAllByTransactionStatusIn(Collection<TransactionStatus> transactionStatus);
}
