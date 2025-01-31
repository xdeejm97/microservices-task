package com.task.commondto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    private Long userId;
    private Long productId;
    private Long amount;
    private Long orderId;

}
