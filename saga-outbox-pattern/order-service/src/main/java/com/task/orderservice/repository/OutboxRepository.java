package com.task.orderservice.repository;

import com.task.orderservice.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OutboxRepository extends JpaRepository<Outbox, Integer> {
}
