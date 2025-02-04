package com.task.orderpoller.service;

import com.task.orderpoller.model.Outbox;
import com.task.orderpoller.repository.OutboxPollerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableScheduling
public class OutboxPollerService {

  private final OutboxPollerRepository outboxRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  @Scheduled(fixedRate = 10000)
  public void fetchOutboxMessages() {

    List<Outbox> unprocessedData = outboxRepository.findByStatusFalse();


    unprocessedData.forEach(outbox -> {
      try {
        kafkaTemplate.send("order-topic", outbox.getPayload());
        outbox.setStatus(true);
        outboxRepository.save(outbox);

      } catch (Exception ignored) {
        log.error(ignored.getMessage());
      }
    });

  }
}
