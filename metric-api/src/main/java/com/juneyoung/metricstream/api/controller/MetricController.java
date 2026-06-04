package com.juneyoung.metricstream.api.controller;

import com.juneyoung.metricstream.api.dto.ApiLogResponse;
import com.juneyoung.metricstream.api.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/metrics")
@RequiredArgsConstructor
public class MetricController {

    private final ApiLogRepository apiLogRepository;

    @GetMapping
    public Page<ApiLogResponse> getAll(
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return apiLogRepository.findAll(pageable).map(ApiLogResponse::from);
    }

    @GetMapping("/{serverId}")
    public Page<ApiLogResponse> getByServer(
            @PathVariable String serverId,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return apiLogRepository.findByServerId(serverId, pageable).map(ApiLogResponse::from);
    }

    @GetMapping("/{serverId}/latest")
    public ResponseEntity<ApiLogResponse> getLatest(@PathVariable String serverId) {
        return apiLogRepository.findTopByServerIdOrderByTimestampDesc(serverId)
                .map(ApiLogResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
