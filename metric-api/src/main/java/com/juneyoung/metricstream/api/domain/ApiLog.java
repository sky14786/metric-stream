package com.juneyoung.metricstream.api.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "api_logs")
@Getter
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
