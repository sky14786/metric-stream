package com.juneyoung.metricstream.consumer.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "api_logs", indexes = {
        @Index(name = "idx_api_logs_timestamp", columnList = "timestamp"),
        @Index(name = "idx_api_logs_server_id", columnList = "serverId"),
        @Index(name = "idx_api_logs_region",    columnList = "region")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) private String requestId;
    @Column(nullable = false) private String serverId;
    @Column(nullable = false) private String region;
    @Column(nullable = false) private String endpoint;
    @Column(nullable = false) private String method;
    @Column(nullable = false) private int statusCode;
    @Column(nullable = false) private int responseTimeMs;
    @Column(nullable = false) private int requestSize;
    @Column(nullable = false) private int responseSize;
    @Column(nullable = false) private String deviceType;
    @Column                   private String errorMessage;
    @Column(nullable = false) private Instant timestamp;

    @Builder
    public ApiLog(String requestId, String serverId, String region, String endpoint,
                  String method, int statusCode, int responseTimeMs, int requestSize,
                  int responseSize, String deviceType, String errorMessage, Instant timestamp) {
        this.requestId     = requestId;
        this.serverId      = serverId;
        this.region        = region;
        this.endpoint      = endpoint;
        this.method        = method;
        this.statusCode    = statusCode;
        this.responseTimeMs = responseTimeMs;
        this.requestSize   = requestSize;
        this.responseSize  = responseSize;
        this.deviceType    = deviceType;
        this.errorMessage  = errorMessage;
        this.timestamp     = timestamp;
    }
}
