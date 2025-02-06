package com.task.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.orderservice.event.OrderCreatedEvent;
import com.task.orderservice.event.PaymentFailedEvent;
import com.task.orderservice.event.PaymentSuccessEvent;
import com.task.paymentservice.enums.PaymentStatus;
import com.task.paymentservice.model.PaymentBalance;
import com.task.paymentservice.model.PaymentTransaction;
import com.task.paymentservice.repository.PaymentRepository;
import com.task.paymentservice.repository.PaymentTransactionRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final PaymentTransactionRepository paymentTransactionRepository;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final ObjectMapper objectMapper;

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

  @KafkaListener(topics = "order-topic", groupId = "payment-service")
  @Transactional
  public void handleOrderCreated(String payload) {

    try {
      OrderCreatedEvent orderCreatedEvent = objectMapper.readValue(payload, OrderCreatedEvent.class);

      PaymentBalance userBalance = paymentRepository.findById(orderCreatedEvent.getUserId()).orElseThrow(
              () -> new RuntimeException(String.format("User not found %s", orderCreatedEvent.getUserId()))
      );
      PaymentTransaction paymentTransaction = new PaymentTransaction();
      paymentTransaction.setOrderId(orderCreatedEvent.getOrderId());
      paymentTransaction.setUserId(orderCreatedEvent.getUserId());
      paymentTransaction.setAmount(orderCreatedEvent.getPrice());

      sendPaymentMessage(orderCreatedEvent, userBalance, paymentTransaction);

    } catch (Exception e) {
      throw new RuntimeException("Error processing order event", e);
    }
  }

  private void sendPaymentMessage(OrderCreatedEvent orderCreatedEvent, PaymentBalance userBalance, PaymentTransaction paymentTransaction) {
    if (orderCreatedEvent.getPrice() <= userBalance.getBalance()) {
      paymentTransaction.setStatus(PaymentStatus.PAYMENT_COMPLETED);
      paymentTransactionRepository.save(paymentTransaction);
      userBalance.setBalance(userBalance.getBalance() - orderCreatedEvent.getPrice());
      paymentRepository.save(userBalance);
      log.info("Payment successful for order: {}", orderCreatedEvent);
      kafkaTemplate.send("payment-success-topic", new PaymentSuccessEvent(orderCreatedEvent.getOrderId(), orderCreatedEvent.getUserId()));
    } else {
      paymentTransaction.setStatus(PaymentStatus.PAYMENT_FAILED);
      paymentTransactionRepository.save(paymentTransaction);
      log.warn("Payment failed for order: {}", orderCreatedEvent);
      kafkaTemplate.send("payment-failed-topic", new PaymentFailedEvent(orderCreatedEvent.getOrderId(), orderCreatedEvent.getUserId()));
    }
  }
}
