package com.traceback.reports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemReportRepository extends MongoRepository<ItemReport, String>, ItemReportRepositoryCustom {

    Page<ItemReport> findByOwnerId(String ownerId, Pageable pageable);

    List<ItemReport> findByOwnerId(String ownerId);

    Page<ItemReport> findByReportTypeAndStatus(ReportType reportType, ReportStatus status, Pageable pageable);

    GeoResults<ItemReport> findByLocationNearAndStatus(Point point, Distance distance, ReportStatus status);

    GeoResults<ItemReport> findByLocationNearAndReportTypeAndStatus(Point point, Distance distance, ReportType reportType, ReportStatus status);

    long countByReportType(ReportType reportType);

    long countByStatus(ReportStatus status);
}
