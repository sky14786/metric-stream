package com.juneyoung.metricstream.api.repository;

import com.juneyoung.metricstream.api.domain.ApiLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApiLogRepository extends JpaRepository<ApiLog, Long> {

    Page<ApiLog> findByServerId(String serverId, Pageable pageable);

    Optional<ApiLog> findTopByServerIdOrderByTimestampDesc(String serverId);
}
