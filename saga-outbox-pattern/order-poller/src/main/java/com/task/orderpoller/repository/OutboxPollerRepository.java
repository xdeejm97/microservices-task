package com.task.orderpoller.repository;

import com.task.orderpoller.model.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxPollerRepository extends JpaRepository<Outbox, Integer> {

  List<Outbox> findByStatusFalse();
}
