package com.task.paymentservice.repository;

import com.task.paymentservice.model.PaymentBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentBalance, Integer> {
}
