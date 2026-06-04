package com.juneyoung.metricstream.consumer.repository;

import com.juneyoung.metricstream.consumer.domain.ApiLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {
}
