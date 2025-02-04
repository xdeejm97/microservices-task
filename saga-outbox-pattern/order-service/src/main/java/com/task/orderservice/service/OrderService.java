package com.task.orderservice.service;

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
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;
  private final OutboxRepository outboxRepository;

  @Transactional
  @SneakyThrows
  public void createOrder(Order order) {
    order.setStatus(OrderStatus.CREATED);
    orderRepository.save(order);

    OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
            .orderId(order.getOrderId())
            .userId(order.getUserId())
            .price(order.getPrice())
            .build();

    Outbox outbox = Outbox.builder()
            .aggregateId(order.getOrderId())
            .payload(new ObjectMapper().writeValueAsString(orderCreatedEvent))
            .status(false)
            .build();

    outboxRepository.save(outbox);

  }

  @KafkaListener(topics = "payment-success-topic", groupId = "order-service")
  @Transactional
  public void handlePaymentSuccess(PaymentSuccessEvent event) {
    orderRepository.findById(event.getOrderId()).ifPresent(order -> {
      order.setStatus(OrderStatus.COMPLETED);
      orderRepository.save(order);
    });
  }

  @KafkaListener(topics = "payment-failed-topic", groupId = "order-service")
  @Transactional
  public void handlePaymentFailed(PaymentFailedEvent event) {
    orderRepository.findById(event.getOrderId()).ifPresent(order -> {
      order.setStatus(OrderStatus.CANCELLED);
      orderRepository.save(order);
    });

  }

}
