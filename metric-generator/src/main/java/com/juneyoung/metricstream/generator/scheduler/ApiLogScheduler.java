package com.juneyoung.metricstream.generator.scheduler;

import com.juneyoung.metricstream.generator.domain.ApiLog;
import com.juneyoung.metricstream.generator.producer.ApiLogProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLogScheduler {

    private final ApiLogProducer producer;

    private static final List<String> SERVERS = List.of(
            "server-001", "server-002", "server-003", "server-004", "server-005",
            "server-006", "server-007", "server-008", "server-009", "server-010",
            "server-011", "server-012", "server-013", "server-014", "server-015",
            "server-016", "server-017", "server-018", "server-019", "server-020"
    );

    // 서버별 리전 고정 (서버는 물리적 위치가 고정됨)
    private static final Map<String, String> SERVER_REGION = Map.ofEntries(
            Map.entry("server-001", "seoul"),  Map.entry("server-002", "seoul"),
            Map.entry("server-003", "seoul"),  Map.entry("server-004", "seoul"),
            Map.entry("server-005", "seoul"),  Map.entry("server-006", "seoul"),
            Map.entry("server-007", "seoul"),  Map.entry("server-008", "seoul"),
            Map.entry("server-009", "busan"),  Map.entry("server-010", "busan"),
            Map.entry("server-011", "busan"),  Map.entry("server-012", "busan"),
            Map.entry("server-013", "busan"),  Map.entry("server-014", "daejeon"),
            Map.entry("server-015", "daejeon"), Map.entry("server-016", "daejeon"),
            Map.entry("server-017", "daejeon"), Map.entry("server-018", "incheon"),
            Map.entry("server-019", "incheon"), Map.entry("server-020", "incheon")
    );

    // [method, endpoint] 쌍
    private static final String[][] ENDPOINTS = {
            {"GET",  "/api/users"},
            {"GET",  "/api/users/{id}"},
            {"POST", "/api/users"},
            {"GET",  "/api/orders"},
            {"POST", "/api/orders"},
            {"GET",  "/api/orders/{id}"},
            {"PUT",  "/api/orders/{id}"},
            {"GET",  "/api/products"},
            {"GET",  "/api/products/{id}"},
            {"POST", "/api/auth/login"},
    };

    // 상태코드 가중치 풀: 200(65%) 201(10%) 400(8%) 401(4%) 404(8%) 500(5%)
    private static final int[] STATUS_POOL = buildStatusPool();

    private static final String[] DEVICE_TYPES = {
            "DESKTOP", "DESKTOP", "DESKTOP", "MOBILE", "MOBILE", "API"
    };

    private static final String[] ERROR_MESSAGES = {
            "Connection timeout",
            "NullPointerException",
            "Database connection failed",
            "Out of memory",
            "Internal server error"
    };

    @Scheduled(fixedDelay = 5000)
    public void generate() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        int count = rnd.nextInt(3, 8);

        for (int i = 0; i < count; i++) {
            String serverId = SERVERS.get(rnd.nextInt(SERVERS.size()));
            String[] ep = ENDPOINTS[rnd.nextInt(ENDPOINTS.length)];
            int statusCode = STATUS_POOL[rnd.nextInt(STATUS_POOL.length)];

            producer.send(ApiLog.builder()
                    .requestId(UUID.randomUUID().toString())
                    .serverId(serverId)
                    .region(SERVER_REGION.get(serverId))
                    .method(ep[0])
                    .endpoint(ep[1])
                    .statusCode(statusCode)
                    .responseTimeMs(randomResponseTime(statusCode, rnd))
                    .requestSize(rnd.nextInt(128, 2048))
                    .responseSize(randomResponseSize(statusCode, rnd))
                    .deviceType(DEVICE_TYPES[rnd.nextInt(DEVICE_TYPES.length)])
                    .errorMessage(statusCode == 500 ? ERROR_MESSAGES[rnd.nextInt(ERROR_MESSAGES.length)] : null)
                    .timestamp(Instant.now())
                    .build());
        }

        log.info("Generated {} logs", count);
    }

    private static int randomResponseTime(int statusCode, ThreadLocalRandom rnd) {
        return switch (statusCode) {
            case 200, 201 -> rnd.nextInt(50, 301);
            case 400, 401, 404 -> rnd.nextInt(20, 101);
            case 500 -> rnd.nextInt(800, 3001);
            default -> rnd.nextInt(50, 301);
        };
    }

    private static int randomResponseSize(int statusCode, ThreadLocalRandom rnd) {
        return (statusCode == 200 || statusCode == 201)
                ? rnd.nextInt(1024, 8193)
                : rnd.nextInt(64, 513);
    }

    private static int[] buildStatusPool() {
        int[] pool = new int[100];
        int idx = 0;
        for (int i = 0; i < 65; i++) pool[idx++] = 200;
        for (int i = 0; i < 10; i++) pool[idx++] = 201;
        for (int i = 0; i < 8;  i++) pool[idx++] = 400;
        for (int i = 0; i < 4;  i++) pool[idx++] = 401;
        for (int i = 0; i < 8;  i++) pool[idx++] = 404;
        for (int i = 0; i < 5;  i++) pool[idx++] = 500;
        return pool;
    }
}
