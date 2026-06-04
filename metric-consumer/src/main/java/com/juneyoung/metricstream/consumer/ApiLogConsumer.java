package com.juneyoung.metricstream.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juneyoung.metricstream.consumer.domain.ApiLog;
import com.juneyoung.metricstream.consumer.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLogConsumer {

    private final ApiLogRepository apiLogRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${metric.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        try {
            ApiLogMessage dto = objectMapper.readValue(message, ApiLogMessage.class);

            apiLogRepository.save(ApiLog.builder()
                    .requestId(dto.requestId())
                    .serverId(dto.serverId())
                    .region(dto.region())
                    .endpoint(dto.endpoint())
                    .method(dto.method())
                    .statusCode(dto.statusCode())
                    .responseTimeMs(dto.responseTimeMs())
                    .requestSize(dto.requestSize())
                    .responseSize(dto.responseSize())
                    .deviceType(dto.deviceType())
                    .errorMessage(dto.errorMessage())
                    .timestamp(dto.timestamp())
                    .build());

            log.debug("Saved | {} {} {}", dto.serverId(), dto.method(), dto.endpoint());
        } catch (Exception e) {
            log.error("Failed to process message: {}", message, e);
        }
    }

    record ApiLogMessage(
            String requestId, String serverId, String region,
            String endpoint, String method, int statusCode,
            int responseTimeMs, int requestSize, int responseSize,
            String deviceType, String errorMessage, Instant timestamp
    ) {}
}
