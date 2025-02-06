package com.task.order.service;

import com.task.order.model.Order;
import com.task.order.event.OrderCreatedEvent;
import com.task.order.event.OrderStatus;
import com.task.order.event.PaymentFailedEvent;
import com.task.order.event.PaymentSuccessEvent;
import com.task.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;

    public void createOrder(Order order) {
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);
        log.info("Order created: {}", order);
        kafkaTemplate.send("order-topic", new OrderCreatedEvent(order.getOrderId(), order.getUserId(), order.getPrice()));
    }

    @KafkaListener(topics = "payment-success-topic", groupId = "order")
    @Transactional
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
            log.info("Order completed: {}", event);
        });
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "order")
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
            log.warn("Order failed: {}", event);
        });
    }

}
