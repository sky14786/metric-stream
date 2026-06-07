package com.juneyoung.metricstream.consumer.scheduler;

import com.juneyoung.metricstream.consumer.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetentionScheduler {

    private static final int RETENTION_DAYS = 3;

    private final ApiLogRepository apiLogRepository;

    // 매일 새벽 3시 실행
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void purgeOldLogs() {
        Instant cutoff = Instant.now().minus(RETENTION_DAYS, ChronoUnit.DAYS);
        int deleted = apiLogRepository.deleteByTimestampBefore(cutoff);
        log.info("Retention purge complete — deleted {} rows older than {}d (cutoff: {})",
                deleted, RETENTION_DAYS, cutoff);
    }
}
