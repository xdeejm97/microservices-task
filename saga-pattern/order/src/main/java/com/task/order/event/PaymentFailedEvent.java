package com.task.order.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentFailedEvent {

    private Integer orderId;
    private Integer userId;
    private Integer price;

}
