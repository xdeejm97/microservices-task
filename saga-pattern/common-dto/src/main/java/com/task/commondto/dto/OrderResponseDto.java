package com.task.commondto.dto;

import com.task.commondto.event.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private Long userId;
    private Long productId;
    private Long amount;
    private Long orderId;
    private OrderStatus orderStatus;

}
