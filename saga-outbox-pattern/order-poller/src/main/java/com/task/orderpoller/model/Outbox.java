package com.task.orderpoller.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
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