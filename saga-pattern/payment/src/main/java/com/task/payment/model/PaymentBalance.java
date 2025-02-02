package com.task.payment.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentBalance {

    @Id
    private Integer userId;
    private Integer balance;

}
