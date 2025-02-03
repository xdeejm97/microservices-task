package com.task.payment.model;

import com.task.payment.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "payment_transactions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentTransaction {

    @Id
    private Integer orderId;
    private int userId;
    private int amount;
    @Enumerated(STRING)
    private PaymentStatus status;

}
