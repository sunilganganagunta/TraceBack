package com.traceback.reports;

import com.traceback.common.ApiResponse;
import com.traceback.common.PagedResponse;
import com.traceback.reports.dto.CreateReportRequest;
import com.traceback.reports.dto.ReportResponseDto;
import com.traceback.reports.dto.UpdateReportRequest;
import com.traceback.security.UserPrincipal;
import com.traceback.users.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports", description = "Lost and Found item reporting, discovery, and geospatial search")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    @Operation(summary = "Create a new Lost or Found report")
    public ResponseEntity<ApiResponse<ReportResponseDto>> createReport(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody CreateReportRequest request) {
        ReportResponseDto created = reportService.createReport(principal.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Report created successfully", created));
    }

    @GetMapping
    @Operation(summary = "Search and filter public item reports (paginated)")
    public ResponseEntity<ApiResponse<PagedResponse<ReportResponseDto>>> searchReports(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ReportType reportType,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) ReportStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        String currentUserId = principal != null ? principal.getId() : null;
        boolean isPrivileged = principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()) || a.getAuthority().equals(Role.ROLE_ADMIN.name()));

        PagedResponse<ReportResponseDto> results = reportService.searchReports(
                keyword, reportType, categoryId, status, startDate, endDate, currentUserId, isPrivileged, pageable);

        return ResponseEntity.ok(ApiResponse.ok(results));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Geospatial proximity discovery of items (2dsphere index)")
    public ResponseEntity<ApiResponse<List<ReportResponseDto>>> findNearby(
            @RequestParam double longitude,
            @RequestParam double latitude,
            @RequestParam(defaultValue = "5000") double radiusMeters,
            @RequestParam(required = false) ReportType reportType,
            @RequestParam(defaultValue = "30") int limit,
            @AuthenticationPrincipal UserPrincipal principal) {

        String currentUserId = principal != null ? principal.getId() : null;
        boolean isPrivileged = principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()) || a.getAuthority().equals(Role.ROLE_ADMIN.name()));

        List<ReportResponseDto> items = reportService.findNearby(
                longitude, latitude, radiusMeters, reportType, limit, currentUserId, isPrivileged);

        return ResponseEntity.ok(ApiResponse.ok(items));
    }

    @GetMapping("/my-reports")
    @Operation(summary = "Get all reports submitted by the authenticated user")
    public ResponseEntity<ApiResponse<PagedResponse<ReportResponseDto>>> getMyReports(
            @AuthenticationPrincipal UserPrincipal principal,
            @PageableDefault(size = 12, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        PagedResponse<ReportResponseDto> reports = reportService.getMyReports(principal.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.ok(reports));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single report by ID (with privacy-aware location masking)")
    public ResponseEntity<ApiResponse<ReportResponseDto>> getReportById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {

        String currentUserId = principal != null ? principal.getId() : null;
        boolean isPrivileged = principal != null && principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()) || a.getAuthority().equals(Role.ROLE_ADMIN.name()));

        ReportResponseDto report = reportService.getReportById(id, currentUserId, isPrivileged);
        return ResponseEntity.ok(ApiResponse.ok(report));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing report")
    public ResponseEntity<ApiResponse<ReportResponseDto>> updateReport(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpdateReportRequest request) {

        boolean isPrivileged = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()) || a.getAuthority().equals(Role.ROLE_ADMIN.name()));

        ReportResponseDto updated = reportService.updateReport(id, principal.getId(), request, isPrivileged);
        return ResponseEntity.ok(ApiResponse.ok("Report updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Change the lifecycle status of a report")
    public ResponseEntity<ApiResponse<ReportResponseDto>> updateStatus(
            @PathVariable String id,
            @RequestParam ReportStatus status,
            @AuthenticationPrincipal UserPrincipal principal) {

        boolean isPrivileged = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()) || a.getAuthority().equals(Role.ROLE_ADMIN.name()));

        ReportResponseDto updated = reportService.updateStatus(id, principal.getId(), status, isPrivileged);
        return ResponseEntity.ok(ApiResponse.ok("Report status updated", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Withdraw a report")
    public ResponseEntity<ApiResponse<Void>> withdrawReport(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {

        boolean isPrivileged = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ROLE_MODERATOR.name()) || a.getAuthority().equals(Role.ROLE_ADMIN.name()));

        reportService.withdrawReport(id, principal.getId(), isPrivileged);
        return ResponseEntity.ok(ApiResponse.ok("Report withdrawn successfully", null));
    }
}
