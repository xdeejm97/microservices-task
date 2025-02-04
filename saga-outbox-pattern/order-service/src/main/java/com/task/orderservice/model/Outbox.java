package com.task.orderservice.model;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "outbox")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Outbox {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  private Integer id;
  private Integer aggregateId;
  private String payload;
  private Boolean status;

}
