package com.traceback.analytics;

import com.traceback.analytics.dto.AnalyticsSummaryDto;
import com.traceback.analytics.dto.CategoryStatDto;
import com.traceback.analytics.dto.StatusStatDto;
import com.traceback.analytics.dto.TimelineStatDto;
import com.traceback.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "MongoDB aggregation analytics and platform recovery metrics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "Get platform overview metrics and recovery rates (MongoDB Aggregations)")
    public ResponseEntity<ApiResponse<AnalyticsSummaryDto>> getSummary() {
        AnalyticsSummaryDto summary = analyticsService.getAnalyticsSummary();
        return ResponseEntity.ok(ApiResponse.ok(summary));
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "Get reports breakdown by category via MongoDB Aggregation")
    public ResponseEntity<ApiResponse<List<CategoryStatDto>>> getCategoryDistribution() {
        List<CategoryStatDto> stats = analyticsService.getCategoryDistribution();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "Get reports status distribution via MongoDB Aggregation")
    public ResponseEntity<ApiResponse<List<StatusStatDto>>> getStatusDistribution() {
        List<StatusStatDto> stats = analyticsService.getStatusDistribution();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/timeline")
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @Operation(summary = "Get timeline report trends via MongoDB Aggregation")
    public ResponseEntity<ApiResponse<List<TimelineStatDto>>> getTimelineTrends() {
        List<TimelineStatDto> trends = analyticsService.getMonthlyTrends();
        return ResponseEntity.ok(ApiResponse.ok(trends));
    }
}
