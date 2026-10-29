package com.traceback.reports;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Repository
public class ItemReportRepositoryImpl implements ItemReportRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public ItemReportRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<ItemReport> searchReports(
            String keyword,
            ReportType reportType,
            String categoryId,
            ReportStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        // Keyword search across title, description, brand, and color
        if (keyword != null && !keyword.trim().isEmpty()) {
            String sanitized = Pattern.quote(keyword.trim());
            Criteria keywordCriteria = new Criteria().orOperator(
                    Criteria.where("title").regex(sanitized, "i"),
                    Criteria.where("description").regex(sanitized, "i"),
                    Criteria.where("brand").regex(sanitized, "i"),
                    Criteria.where("color").regex(sanitized, "i"),
                    Criteria.where("distinguishingFeatures").regex(sanitized, "i")
            );
            criteriaList.add(keywordCriteria);
        }

        if (reportType != null) {
            criteriaList.add(Criteria.where("reportType").is(reportType));
        }

        if (categoryId != null && !categoryId.trim().isEmpty()) {
            criteriaList.add(Criteria.where("categoryId").is(categoryId));
        }

        if (status != null) {
            criteriaList.add(Criteria.where("status").is(status));
        } else {
            // By default, exclude withdrawn and closed reports from general search
            criteriaList.add(Criteria.where("status").nin(ReportStatus.WITHDRAWN, ReportStatus.CLOSED));
        }

        if (startDate != null && endDate != null) {
            criteriaList.add(Criteria.where("dateLostOrFound").gte(startDate).lte(endDate));
        } else if (startDate != null) {
            criteriaList.add(Criteria.where("dateLostOrFound").gte(startDate));
        } else if (endDate != null) {
            criteriaList.add(Criteria.where("dateLostOrFound").lte(endDate));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        long total = mongoTemplate.count(query, ItemReport.class);
        query.with(pageable);
        List<ItemReport> list = mongoTemplate.find(query, ItemReport.class);

        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public List<ItemReport> findNearby(
            double longitude,
            double latitude,
            double radiusMeters,
            ReportType reportType,
            int limit) {

        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        // radius in kilometers: meters / 1000.0
        double radiusKm = radiusMeters / 1000.0;

        NearQuery nearQuery = NearQuery.near(point, Metrics.KILOMETERS)
                .maxDistance(radiusKm)
                .limit(limit > 0 ? limit : 50);

        Query query = new Query();
        if (reportType != null) {
            query.addCriteria(Criteria.where("reportType").is(reportType));
        }
        query.addCriteria(Criteria.where("status").nin(ReportStatus.WITHDRAWN, ReportStatus.CLOSED));
        nearQuery.query(query);

        return mongoTemplate.geoNear(nearQuery, ItemReport.class)
                .getContent()
                .stream()
                .map(geoResult -> geoResult.getContent())
                .toList();
    }
}
