package com.task.payment.repository;

import com.task.payment.model.PaymentBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentBalance, Integer> {
}
