package com.shortify.urlservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Redis Cache Configuration implementing Cache-Aside Pattern.
 *
 * CACHE STRATEGY FOR URL SHORTENER:
 *
 * Cache name "urls":
 *   - Key:   shortCode (String)
 *   - Value: originalUrl (String)
 *   - TTL:   24 hours
 *   - Rationale: Most URLs receive the bulk of their clicks in the first 24 hours
 *     (content goes viral, then fades). 24h TTL balances memory usage vs hit rate.
 *     Hot URLs that stay popular will be re-cached on next miss.
 *
 * WHAT WE DON'T CACHE:
 * - User dashboard data (changes frequently, personalized, lower traffic)
 * - Analytics data (always fresh required)
 * - URL existence checks (low latency DB query is fine)
 *
 * CACHE EVICTION:
 * - On URL deletion: @CacheEvict removes the entry immediately.
 * - On URL update: @CachePut updates the cache entry.
 * - Redis TTL eviction: Automatic after 24 hours (LRU for memory pressure).
 */
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        GenericJackson2JsonRedisSerializer jsonSerializer =
                new GenericJackson2JsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer)
                )
                .disableCachingNullValues(); // Don't cache 404s — attacker could exhaust cache

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .build();
    }
}
