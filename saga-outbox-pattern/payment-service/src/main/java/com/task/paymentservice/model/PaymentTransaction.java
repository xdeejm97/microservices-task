package com.task.paymentservice.model;

import com.task.paymentservice.enums.PaymentStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
