package com.juneyoung.metricstream.generator.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juneyoung.metricstream.generator.domain.ApiLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLogProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${metric.kafka.topic}")
    private String topic;

    public void send(ApiLog apiLog) {
        try {
            String message = objectMapper.writeValueAsString(apiLog);
            kafkaTemplate.send(topic, apiLog.getServerId(), message);
            log.debug("Sent | {} {} {} {}ms", apiLog.getServerId(), apiLog.getMethod(), apiLog.getEndpoint(), apiLog.getResponseTimeMs());
        } catch (JsonProcessingException e) {
            log.error("Serialization failed for serverId={}", apiLog.getServerId(), e);
        }
    }
}
