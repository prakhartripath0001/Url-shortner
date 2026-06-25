package com.shortify.analyticsservice.consumer;

import com.shortify.analyticsservice.entity.ClickEvent;
import com.shortify.analyticsservice.event.UrlClickedEvent;
import com.shortify.analyticsservice.repository.ClickEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka consumer that processes URL click events from the url-service.
 *
 * CONSUMER CONFIGURATION:
 *
 * groupId = "analytics-service":
 *   - All instances of analytics-service share this group ID.
 *   - Kafka distributes partitions across instances automatically.
 *   - Example: 6 partitions, 3 instances → 2 partitions per instance.
 *
 * ackMode = MANUAL_IMMEDIATE:
 *   - We manually commit the offset AFTER successful processing.
 *   - Prevents data loss if the service crashes mid-processing.
 *   - If processing fails, we do NOT commit → Kafka will redeliver.
 *
 * WHY NOT AUTO-COMMIT?
 *   - Auto-commit acknowledges the offset before processing completes.
 *   - If the service crashes after auto-commit but before DB write,
 *     the click event is LOST permanently.
 *   - Manual commit gives us at-least-once delivery guarantee.
 *
 * IDEMPOTENCY:
 *   - With at-least-once delivery, duplicate events are possible on retry.
 *   - We should check for duplicate click IDs before inserting.
 *   - For now, we accept rare duplicates (they barely affect analytics accuracy).
 *   - Production: use exactly-once semantics with Kafka transactions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UrlClickedConsumer {

    private final ClickEventRepository clickEventRepository;

    @KafkaListener(
        topics = "${app.kafka.topics.url-clicked}",
        groupId = "analytics-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consume(
            @Payload UrlClickedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment
    ) {
        log.debug("Processing click event: shortCode={}, partition={}, offset={}",
                event.shortCode(), partition, offset);

        try {
            ClickEvent clickEvent = ClickEvent.builder()
                    .shortCode(event.shortCode())
                    .urlId(event.urlId() != null ? event.urlId() : 0L)
                    .userId(event.userId() != null ? event.userId() : 0L)
                    .ipAddress(event.ipAddress())
                    .userAgent(event.userAgent())
                    .referer(event.referer())
                    .country(event.country())
                    .city(event.city())
                    .deviceType(event.deviceType())
                    .clickedAt(event.clickedAt())
                    .build();

            clickEventRepository.save(clickEvent);

            // Manually acknowledge AFTER successful DB write
            acknowledgment.acknowledge();
            log.debug("Successfully processed click event for shortCode: {}", event.shortCode());

        } catch (Exception e) {
            log.error("Failed to process click event for shortCode: {}", event.shortCode(), e);
            // Do NOT acknowledge — Kafka will redeliver this message
            // After max retries, message goes to Dead Letter Topic (DLT)
            throw e;
        }
    }
}
