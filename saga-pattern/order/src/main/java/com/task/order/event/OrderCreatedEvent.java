package com.task.order.event;

import lombok.*;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderCreatedEvent {

    private Integer orderId;
    private Integer userId;
    private Integer price;

}
