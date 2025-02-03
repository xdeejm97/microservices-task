package com.task.orderservice.service;

import com.task.orderservice.event.OrderCreatedEvent;
import com.task.orderservice.event.OrderStatus;
import com.task.orderservice.event.PaymentFailedEvent;
import com.task.orderservice.event.PaymentSuccessEvent;
import com.task.orderservice.model.Order;
import com.task.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;

    public void createOrder(Order order) {
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);

        kafkaTemplate.send("order-topic", new OrderCreatedEvent(order.getOrderId(), order.getUserId(), order.getPrice()));
    }

    @KafkaListener(topics = "payment-success-topic", groupId = "order")
    @Transactional
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
        });
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "order")
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        });
    }

}
