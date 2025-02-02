package com.task.order;

import com.task.order.event.OrderCreatedEvent;
import com.task.order.event.OrderStatus;
import com.task.order.event.PaymentFailedEvent;
import com.task.order.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;

    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);

        kafkaTemplate.send("order-topic", new OrderCreatedEvent(order.getOrderId(), order.getUserId(), order.getPrice()));
        return order;
    }

    @KafkaListener(topics = "payment-success-topic", groupId = "order")
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);
        });
    }

    @KafkaListener(topics = "payment-failed-topic", groupId = "order")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        orderRepository.findById(event.getOrderId()).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        });
    }

}
