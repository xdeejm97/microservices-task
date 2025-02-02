package com.task.payment;

import com.task.payment.model.PaymentBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentBalance, Integer> {
}
