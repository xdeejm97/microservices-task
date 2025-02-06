package com.task.orderservice.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentFailedEvent {

    private Integer orderId;
    private Integer userId;

}
