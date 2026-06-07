package com.juneyoung.metricstream.generator.scheduler;

import com.juneyoung.metricstream.generator.domain.ApiLog;
import com.juneyoung.metricstream.generator.producer.ApiLogProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
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

    private static final Map<String, String> SERVER_REGION = Map.ofEntries(
            Map.entry("server-001", "seoul"),   Map.entry("server-002", "seoul"),
            Map.entry("server-003", "seoul"),   Map.entry("server-004", "seoul"),
            Map.entry("server-005", "seoul"),   Map.entry("server-006", "seoul"),
            Map.entry("server-007", "seoul"),   Map.entry("server-008", "seoul"),
            Map.entry("server-009", "busan"),   Map.entry("server-010", "busan"),
            Map.entry("server-011", "busan"),   Map.entry("server-012", "busan"),
            Map.entry("server-013", "busan"),   Map.entry("server-014", "daejeon"),
            Map.entry("server-015", "daejeon"), Map.entry("server-016", "daejeon"),
            Map.entry("server-017", "daejeon"), Map.entry("server-018", "incheon"),
            Map.entry("server-019", "incheon"), Map.entry("server-020", "incheon")
    );

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

    private static final String[] ERROR_MESSAGES = {
            "Connection timeout",
            "NullPointerException",
            "Database connection failed",
            "Out of memory",
            "Internal server error"
    };

    // 정상 상태: 200(65%) 201(10%) 400(8%) 401(4%) 404(8%) 500(5%)
    private static final int[] STATUS_POOL = buildStatusPool();

    // 불안정 상태: 500(30%) 400(20%) 401(10%) 404(5%) 200(30%) 201(5%)
    private static final int[] DEGRADED_STATUS_POOL = buildDegradedStatusPool();

    // 현재 불안정 서버 목록 (90초마다 갱신)
    private final Set<String> degradedServers = ConcurrentHashMap.newKeySet();

    // ── 로그 생성 ──────────────────────────────────────────

    @Scheduled(fixedDelay = 1000)
    public void generate() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        double multiplier = trafficMultiplier();
        boolean spike     = rnd.nextInt(100) < 3; // 3% 확률로 트래픽 스파이크

        int base  = rnd.nextInt(10, 21);
        int count = spike
                ? (int)(base * multiplier * rnd.nextInt(3, 6))
                : (int)(base * multiplier);
        count = Math.max(1, count);

        for (int i = 0; i < count; i++) {
            String serverId = SERVERS.get(rnd.nextInt(SERVERS.size()));
            boolean degraded = degradedServers.contains(serverId);
            String[] ep      = ENDPOINTS[rnd.nextInt(ENDPOINTS.length)];
            int[] pool       = degraded ? DEGRADED_STATUS_POOL : STATUS_POOL;
            int statusCode   = pool[rnd.nextInt(pool.length)];

            producer.send(ApiLog.builder()
                    .requestId(UUID.randomUUID().toString())
                    .serverId(serverId)
                    .region(SERVER_REGION.get(serverId))
                    .method(ep[0])
                    .endpoint(ep[1])
                    .statusCode(statusCode)
                    .responseTimeMs(randomResponseTime(statusCode, degraded, rnd))
                    .requestSize(rnd.nextInt(128, 2048))
                    .responseSize(randomResponseSize(statusCode, rnd))
                    .deviceType(randomDeviceType(rnd))
                    .errorMessage(statusCode >= 500 ? ERROR_MESSAGES[rnd.nextInt(ERROR_MESSAGES.length)] : null)
                    .timestamp(Instant.now())
                    .build());
        }

        if (spike) log.warn("Traffic spike! count={} (x{} multiplier)", count, String.format("%.1f", multiplier));
        else       log.debug("Generated {} logs (multiplier={})", count, String.format("%.2f", multiplier));
    }

    // ── 서버 건강 상태 갱신 (90초마다) ────────────────────

    @Scheduled(fixedDelay = 90_000)
    public void updateServerHealth() {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();
        degradedServers.clear();
        int n = rnd.nextInt(3); // 0~2개 서버 불안정
        for (int i = 0; i < n; i++) {
            degradedServers.add(SERVERS.get(rnd.nextInt(SERVERS.size())));
        }
        if (!degradedServers.isEmpty()) {
            log.warn("Server health update — degraded: {}", degradedServers);
        }
    }

    // ── 헬퍼 ───────────────────────────────────────────────

    // 시간대별 트래픽 배율
    private static double trafficMultiplier() {
        return switch (LocalTime.now().getHour()) {
            case 0, 1, 2, 3, 4, 5 -> 0.3;
            case 6, 7              -> 0.6;
            case 8, 9, 10          -> 1.5;   // 오전 피크
            case 11, 12            -> 1.0;
            case 13, 14            -> 1.3;   // 점심 후 피크
            case 15, 16            -> 1.0;
            case 17, 18            -> 1.4;   // 퇴근 피크
            case 19, 20, 21        -> 0.8;
            default                -> 0.4;   // 22, 23시
        };
    }

    // 낮: DESKTOP 중심 / 밤: MOBILE 중심
    private static String randomDeviceType(ThreadLocalRandom rnd) {
        int hour = LocalTime.now().getHour();
        String[] pool = (hour >= 9 && hour <= 18)
                ? new String[]{"DESKTOP", "DESKTOP", "DESKTOP", "MOBILE", "API"}
                : new String[]{"DESKTOP", "MOBILE",  "MOBILE",  "MOBILE", "API"};
        return pool[rnd.nextInt(pool.length)];
    }

    private static int randomResponseTime(int statusCode, boolean degraded, ThreadLocalRandom rnd) {
        int base = switch (statusCode) {
            case 200, 201      -> rnd.nextInt(50, 301);
            case 400, 401, 404 -> rnd.nextInt(20, 101);
            case 500           -> rnd.nextInt(800, 3001);
            default            -> rnd.nextInt(50, 301);
        };
        // 불안정 서버는 응답시간 1.5~2.5배
        return degraded ? (int)(base * (1.5 + rnd.nextDouble())) : base;
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

    private static int[] buildDegradedStatusPool() {
        int[] pool = new int[100];
        int idx = 0;
        for (int i = 0; i < 30; i++) pool[idx++] = 200;
        for (int i = 0; i < 5;  i++) pool[idx++] = 201;
        for (int i = 0; i < 20; i++) pool[idx++] = 400;
        for (int i = 0; i < 10; i++) pool[idx++] = 401;
        for (int i = 0; i < 5;  i++) pool[idx++] = 404;
        for (int i = 0; i < 30; i++) pool[idx++] = 500;
        return pool;
    }
}
