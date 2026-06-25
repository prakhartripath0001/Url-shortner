package com.shortify.urlservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

/**
 * Kafka Producer Configuration.
 *
 * KEY PRODUCER SETTINGS EXPLAINED:
 *
 * acks=all (acks=-1):
 *   - Producer waits for ALL in-sync replicas to confirm the write.
 *   - Guarantees no data loss if the leader broker fails.
 *   - Tradeoff: slightly higher latency (adds ~2-5ms) — acceptable for our use case.
 *
 * retries=3:
 *   - Retries on transient network errors.
 *   - Combined with idempotent producer, retries don't cause duplicates.
 *
 * enable.idempotence=true:
 *   - Each message gets a unique sequence number.
 *   - Broker deduplicates retried messages → exactly-once semantics on the producer side.
 *   - Required for acks=all and retries > 0.
 *
 * TOPIC CONFIGURATION:
 * - url-clicked: 6 partitions (high throughput, 1M+ events/day)
 *   Partitioned by shortCode → all clicks for same URL go to same partition → ordered analytics
 * - url-created: 3 partitions (lower throughput, 1M URLs/day)
 *
 * REPLICATION FACTOR = 3:
 *   - Minimum for production Kafka.
 *   - Tolerates 1 broker failure without data loss.
 *   - In local dev (single broker), we use replication factor 1.
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.RETRIES_CONFIG, 3,
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true,
                // Batch settings for throughput optimisation
                ProducerConfig.BATCH_SIZE_CONFIG, 16384,        // 16KB batches
                ProducerConfig.LINGER_MS_CONFIG, 5,             // Wait 5ms to fill batch
                ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy" // Snappy: fast + good compression
        ));
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public NewTopic urlClickedTopic() {
        return TopicBuilder.name("url.clicked")
                .partitions(6)
                .replicas(1) // Use 3 in production
                .build();
    }

    @Bean
    public NewTopic urlCreatedTopic() {
        return TopicBuilder.name("url.created")
                .partitions(3)
                .replicas(1) // Use 3 in production
                .build();
    }
}
