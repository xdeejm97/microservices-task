package com.task.orderservice.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentSuccessEvent {

    private Integer orderId;
    private Integer userId;

}
