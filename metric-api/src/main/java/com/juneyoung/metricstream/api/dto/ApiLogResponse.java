package com.juneyoung.metricstream.api.dto;

import com.juneyoung.metricstream.api.domain.ApiLog;

import java.time.Instant;

public record ApiLogResponse(
        Long id,
        String requestId,
        String serverId,
        String region,
        String endpoint,
        String method,
        int statusCode,
        int responseTimeMs,
        int requestSize,
        int responseSize,
        String deviceType,
        String errorMessage,
        Instant timestamp
) {
    public static ApiLogResponse from(ApiLog entity) {
        return new ApiLogResponse(
                entity.getId(),
                entity.getRequestId(),
                entity.getServerId(),
                entity.getRegion(),
                entity.getEndpoint(),
                entity.getMethod(),
                entity.getStatusCode(),
                entity.getResponseTimeMs(),
                entity.getRequestSize(),
                entity.getResponseSize(),
                entity.getDeviceType(),
                entity.getErrorMessage(),
                entity.getTimestamp()
        );
    }
}
