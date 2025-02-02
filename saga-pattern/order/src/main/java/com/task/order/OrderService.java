package com.task.order;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderService {

//    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OrderRepository orderRepository;

    public Order createOrder(Order order) {
//        order.setStatus(OrderStatus.CREATED);
        orderRepository.save(order);

//        kafkaTemplate.send("order-created-topic", new OrderCreated);
        return order;
    }
}
