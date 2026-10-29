package com.traceback.reports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ItemReportRepositoryCustom {

    Page<ItemReport> searchReports(
            String keyword,
            ReportType reportType,
            String categoryId,
            ReportStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    List<ItemReport> findNearby(double longitude, double latitude, double radiusMeters, ReportType reportType, int limit);
}
