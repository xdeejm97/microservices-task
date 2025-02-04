package com.task.paymentservice.repository;

import com.task.paymentservice.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer> {
}
