package com.task.payment.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PaymentBalance implements Serializable {

    @Id
    private Integer userId;
    private Integer balance;

}
