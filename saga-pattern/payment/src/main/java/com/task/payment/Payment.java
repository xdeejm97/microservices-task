package com.task.payment;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private BigDecimal amount;
    private PaymentStatus status;
}
