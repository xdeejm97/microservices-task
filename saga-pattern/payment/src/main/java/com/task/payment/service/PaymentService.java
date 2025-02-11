package com.task.payment.service;

import com.task.order.event.OrderCreatedEvent;
import com.task.order.event.PaymentFailedEvent;
import com.task.order.event.PaymentSuccessEvent;
import com.task.payment.enums.PaymentStatus;
import com.task.payment.repository.PaymentTransactionRepository;
import com.task.payment.model.PaymentBalance;
import com.task.payment.model.PaymentTransaction;
import com.task.payment.repository.PaymentRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostConstruct
    public void init() {
        paymentRepository.saveAll(Stream.of(
                        new PaymentBalance(1, 1000),
                        new PaymentBalance(2, 999),
                        new PaymentBalance(3, 1500),
                        new PaymentBalance(4, 1200)
                ).toList()
        );
    }

    @KafkaListener(topics = "order-topic", groupId = "payment")
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent orderCreatedEvent) {
        PaymentBalance userBalance = paymentRepository.findById(orderCreatedEvent.getUserId()).orElseThrow(
                () -> new RuntimeException(String.format("User not found %s", orderCreatedEvent.getUserId()))
        );
        PaymentTransaction paymentTransaction = new PaymentTransaction();
        paymentTransaction.setOrderId(orderCreatedEvent.getOrderId());
        paymentTransaction.setUserId(orderCreatedEvent.getUserId());
        paymentTransaction.setAmount(orderCreatedEvent.getPrice());

        sendPaymentMessage(orderCreatedEvent, userBalance, paymentTransaction);

    }

    private void sendPaymentMessage(OrderCreatedEvent orderCreatedEvent, PaymentBalance userBalance, PaymentTransaction paymentTransaction) {
        if (orderCreatedEvent.getPrice() <= userBalance.getBalance()) {
            paymentTransaction.setStatus(PaymentStatus.PAYMENT_COMPLETED);
            paymentTransactionRepository.save(paymentTransaction);
            userBalance.setBalance(userBalance.getBalance() - orderCreatedEvent.getPrice());
            paymentRepository.save(userBalance);
            log.info("Payment successful for order: {}", orderCreatedEvent);
            kafkaTemplate.send("payment-success-topic", new PaymentSuccessEvent(orderCreatedEvent.getOrderId(), orderCreatedEvent.getUserId(), orderCreatedEvent.getPrice()));
        } else {
            paymentTransaction.setStatus(PaymentStatus.PAYMENT_FAILED);
            paymentTransactionRepository.save(paymentTransaction);
            log.warn("Payment failed for order: {}", orderCreatedEvent);
            kafkaTemplate.send("payment-failed-topic", new PaymentFailedEvent(orderCreatedEvent.getOrderId(), orderCreatedEvent.getUserId(), orderCreatedEvent.getPrice()));
        }
    }

}
