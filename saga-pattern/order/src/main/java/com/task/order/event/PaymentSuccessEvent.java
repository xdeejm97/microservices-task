package com.task.order.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentSuccessEvent {

    private Integer orderId;
    private Integer userId;

}
