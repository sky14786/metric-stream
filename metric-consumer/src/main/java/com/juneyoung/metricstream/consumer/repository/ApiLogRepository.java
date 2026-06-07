package com.juneyoung.metricstream.consumer.repository;

import com.juneyoung.metricstream.consumer.domain.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {

    @Modifying
    @Query("DELETE FROM ApiLog a WHERE a.timestamp < :cutoff")
    int deleteByTimestampBefore(@Param("cutoff") Instant cutoff);
}
