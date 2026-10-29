package com.traceback.reports;

import com.traceback.categories.Category;
import com.traceback.categories.CategoryRepository;
import com.traceback.common.ForbiddenException;
import com.traceback.common.PagedResponse;
import com.traceback.common.ResourceNotFoundException;
import com.traceback.matching.Match;
import com.traceback.matching.MatchingEngineService;
import com.traceback.notifications.NotificationService;
import com.traceback.notifications.NotificationType;
import com.traceback.reports.dto.CreateReportRequest;
import com.traceback.reports.dto.ReportResponseDto;
import com.traceback.reports.dto.UpdateReportRequest;
import com.traceback.users.User;
import com.traceback.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    private static final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final ItemReportRepository reportRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final MatchingEngineService matchingEngineService;
    private final NotificationService notificationService;

    public ReportService(ItemReportRepository reportRepository,
                         CategoryRepository categoryRepository,
                         UserRepository userRepository,
                         MatchingEngineService matchingEngineService,
                         NotificationService notificationService) {
        this.reportRepository = reportRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.matchingEngineService = matchingEngineService;
        this.notificationService = notificationService;
    }

    public ReportResponseDto createReport(String userId, CreateReportRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));

        ItemReport report = new ItemReport();
        report.setOwnerId(userId);
        report.setOwnerName(user.getName());
        report.setReportType(request.getReportType());
        report.setTitle(request.getTitle().trim());
        report.setCategoryId(category.getId());
        report.setCategoryName(category.getName());
        report.setDescription(request.getDescription().trim());
        report.setColor(request.getColor().trim());
        report.setBrand(request.getBrand() != null ? request.getBrand().trim() : null);
        report.setDistinguishingFeatures(request.getDistinguishingFeatures() != null ? request.getDistinguishingFeatures().trim() : null);
        report.setDateLostOrFound(request.getDateLostOrFound());
        report.setLocation(new GeoJsonPoint(request.getLongitude(), request.getLatitude()));
        report.setPublicLocationLabel(request.getPublicLocationLabel().trim());
        report.setImages(request.getImages() != null ? request.getImages() : List.of());
        report.setStatus(ReportStatus.ACTIVE);
        report.setVisibility(Visibility.PUBLIC);
        report.setCreatedAt(Instant.now());
        report.setUpdatedAt(Instant.now());

        ItemReport savedReport = reportRepository.save(report);

        // Run matching engine asynchronously/synchronously
        try {
            List<Match> matches = matchingEngineService.calculateMatchesForReport(savedReport);
            if (!matches.isEmpty()) {
                // If high score matches found, send in-app notification
                for (Match match : matches) {
                    if (match.getScore() >= 55.0) {
                        String opposingReportId = savedReport.getReportType() == ReportType.LOST
                                ? match.getFoundReportId() : match.getLostReportId();

                        reportRepository.findById(opposingReportId).ifPresent(opposing -> {
                            notificationService.createNotification(
                                    opposing.getOwnerId(),
                                    NotificationType.MATCH_FOUND,
                                    "Potential Match Discovered!",
                                    String.format("A new %s report ('%s') matches your item with score %.0f%%.",
                                            savedReport.getReportType(), savedReport.getTitle(), match.getScore()),
                                    match.getId(),
                                    "MATCH"
                            );
                        });
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to run matching engine for report {}", savedReport.getId(), e);
        }

        return ReportResponseDto.from(savedReport, userId, true);
    }

    public PagedResponse<ReportResponseDto> searchReports(
            String keyword,
            ReportType reportType,
            String categoryId,
            ReportStatus status,
            LocalDate startDate,
            LocalDate endDate,
            String currentUserId,
            boolean isPrivileged,
            Pageable pageable) {

        Page<ItemReport> page = reportRepository.searchReports(
                keyword, reportType, categoryId, status, startDate, endDate, pageable);

        Page<ReportResponseDto> dtoPage = page.map(r -> ReportResponseDto.from(r, currentUserId, isPrivileged));
        return PagedResponse.from(dtoPage);
    }

    public List<ReportResponseDto> findNearby(
            double longitude,
            double latitude,
            double radiusMeters,
            ReportType reportType,
            int limit,
            String currentUserId,
            boolean isPrivileged) {

        List<ItemReport> nearby = reportRepository.findNearby(longitude, latitude, radiusMeters, reportType, limit);
        return nearby.stream()
                .map(r -> ReportResponseDto.from(r, currentUserId, isPrivileged))
                .toList();
    }

    public PagedResponse<ReportResponseDto> getMyReports(String userId, Pageable pageable) {
        Page<ItemReport> page = reportRepository.findByOwnerId(userId, pageable);
        Page<ReportResponseDto> dtoPage = page.map(r -> ReportResponseDto.from(r, userId, true));
        return PagedResponse.from(dtoPage);
    }

    public ReportResponseDto getReportById(String id, String currentUserId, boolean isPrivileged) {
        ItemReport report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemReport", "id", id));
        return ReportResponseDto.from(report, currentUserId, isPrivileged);
    }

    public ReportResponseDto updateReport(String id, String userId, UpdateReportRequest request, boolean isPrivileged) {
        ItemReport report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemReport", "id", id));

        if (!report.getOwnerId().equals(userId) && !isPrivileged) {
            throw new ForbiddenException("You do not have permission to modify this report");
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            report.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null && !request.getDescription().isBlank()) {
            report.setDescription(request.getDescription().trim());
        }
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            report.setCategoryId(category.getId());
            report.setCategoryName(category.getName());
        }
        if (request.getColor() != null) report.setColor(request.getColor().trim());
        if (request.getBrand() != null) report.setBrand(request.getBrand().trim());
        if (request.getDistinguishingFeatures() != null) report.setDistinguishingFeatures(request.getDistinguishingFeatures().trim());
        if (request.getPublicLocationLabel() != null) report.setPublicLocationLabel(request.getPublicLocationLabel().trim());

        if (request.getLongitude() != null && request.getLatitude() != null) {
            report.setLocation(new GeoJsonPoint(request.getLongitude(), request.getLatitude()));
        }

        if (request.getImages() != null) report.setImages(request.getImages());
        if (request.getStatus() != null) report.setStatus(request.getStatus());
        if (request.getVisibility() != null) report.setVisibility(request.getVisibility());

        report.setUpdatedAt(Instant.now());
        ItemReport saved = reportRepository.save(report);

        // Recalculate matches
        matchingEngineService.calculateMatchesForReport(saved);

        return ReportResponseDto.from(saved, userId, isPrivileged);
    }

    public ReportResponseDto updateStatus(String id, String userId, ReportStatus newStatus, boolean isPrivileged) {
        ItemReport report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemReport", "id", id));

        if (!report.getOwnerId().equals(userId) && !isPrivileged) {
            throw new ForbiddenException("You do not have permission to change the status of this report");
        }

        report.setStatus(newStatus);
        report.setUpdatedAt(Instant.now());
        return ReportResponseDto.from(reportRepository.save(report), userId, isPrivileged);
    }

    public void withdrawReport(String id, String userId, boolean isPrivileged) {
        ItemReport report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ItemReport", "id", id));

        if (!report.getOwnerId().equals(userId) && !isPrivileged) {
            throw new ForbiddenException("You do not have permission to withdraw this report");
        }

        report.setStatus(ReportStatus.WITHDRAWN);
        report.setUpdatedAt(Instant.now());
        reportRepository.save(report);
    }
}
