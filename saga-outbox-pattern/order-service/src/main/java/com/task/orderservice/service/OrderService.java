package com.task.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.orderservice.event.OrderCreatedEvent;
import com.task.orderservice.event.OrderStatus;
import com.task.orderservice.event.PaymentFailedEvent;
import com.task.orderservice.event.PaymentSuccessEvent;
import com.task.orderservice.model.Order;
import com.task.orderservice.model.Outbox;
import com.task.orderservice.repository.OrderRepository;
import com.task.orderservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

  private final OrderRepository orderRepository;
  private final OutboxRepository outboxRepository;
  private final ObjectMapper objectMapper;

  @Transactional
  public void createOrder(Order order) throws JsonProcessingException {
    order.setStatus(OrderStatus.CREATED);
    orderRepository.save(order);
    saveToOutbox(order);
    log.info("Order created: {}", order);
  }

  private void saveToOutbox(Order order) throws JsonProcessingException {
    OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
            .orderId(order.getOrderId())
            .userId(order.getUserId())
            .price(order.getPrice())
            .build();

    Outbox outbox = Outbox.builder()
            .aggregateId(order.getOrderId())
            .payload(objectMapper.writeValueAsString(orderCreatedEvent))
            .status(false)
            .build();
    outboxRepository.save(outbox);
  }

  @Transactional
  @KafkaListener(topics = "payment-success-topic", groupId = "order-service")
  public void handlePaymentSuccess(PaymentSuccessEvent event) {
    orderRepository.findById(event.getOrderId()).ifPresent(order -> {
      order.setStatus(OrderStatus.COMPLETED);
      orderRepository.save(order);
      log.info("Order completed: {}", event);
    });
  }

  @Transactional
  @KafkaListener(topics = "payment-failed-topic", groupId = "order-service")
  public void handlePaymentFailed(PaymentFailedEvent event) {
    orderRepository.findById(event.getOrderId()).ifPresent(order -> {
      order.setStatus(OrderStatus.CANCELLED);
      orderRepository.save(order);
      log.warn("Payment failed: {}", event);
    });

  }

}
