package com.task.order.model;

import com.task.order.event.OrderStatus;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer orderId;
    private Integer userId;
    private Integer price;
    @Enumerated(STRING)
    private OrderStatus status;

}