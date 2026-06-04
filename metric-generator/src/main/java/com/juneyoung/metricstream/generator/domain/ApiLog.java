package com.juneyoung.metricstream.generator.domain;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ApiLog {
    private String requestId;
    private String serverId;
    private String region;
    private String endpoint;
    private String method;
    private int statusCode;
    private int responseTimeMs;
    private int requestSize;
    private int responseSize;
    private String deviceType;
    private String errorMessage;
    private Instant timestamp;
}
