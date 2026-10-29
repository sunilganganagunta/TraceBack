package com.traceback.analytics;

import com.traceback.analytics.dto.AnalyticsSummaryDto;
import com.traceback.analytics.dto.CategoryStatDto;
import com.traceback.analytics.dto.StatusStatDto;
import com.traceback.analytics.dto.TimelineStatDto;
import com.traceback.claims.Claim;
import com.traceback.claims.ClaimRepository;
import com.traceback.claims.ClaimStatus;
import com.traceback.matching.Match;
import com.traceback.matching.MatchRepository;
import com.traceback.reports.ItemReport;
import com.traceback.reports.ItemReportRepository;
import com.traceback.reports.ReportStatus;
import com.traceback.reports.ReportType;
import com.traceback.users.UserRepository;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsService {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final ItemReportRepository reportRepository;
    private final ClaimRepository claimRepository;
    private final MatchRepository matchRepository;

    public AnalyticsService(MongoTemplate mongoTemplate,
                            UserRepository userRepository,
                            ItemReportRepository reportRepository,
                            ClaimRepository claimRepository,
                            MatchRepository matchRepository) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.claimRepository = claimRepository;
        this.matchRepository = matchRepository;
    }

    public AnalyticsSummaryDto getAnalyticsSummary() {
        AnalyticsSummaryDto summary = new AnalyticsSummaryDto();

        long usersCount = userRepository.count();
        long reportsCount = reportRepository.count();
        long lostCount = reportRepository.countByReportType(ReportType.LOST);
        long foundCount = reportRepository.countByReportType(ReportType.FOUND);
        long activeCount = reportRepository.countByStatus(ReportStatus.ACTIVE);
        long returnedCount = reportRepository.countByStatus(ReportStatus.RETURNED);

        long claimsCount = claimRepository.count();
        long pendingClaimsCount = claimRepository.countByStatus(ClaimStatus.PENDING);
        long approvedClaimsCount = claimRepository.countByStatus(ClaimStatus.APPROVED);

        long matchesCount = matchRepository.count();

        double recoveryRate = (reportsCount > 0)
                ? Math.round((double) returnedCount / reportsCount * 1000.0) / 10.0
                : 0.0;

        summary.setTotalUsers(usersCount);
        summary.setTotalReports(reportsCount);
        summary.setTotalLostReports(lostCount);
        summary.setTotalFoundReports(foundCount);
        summary.setActiveReports(activeCount);
        summary.setRecoveredItems(returnedCount);
        summary.setRecoveryRate(recoveryRate);

        summary.setTotalClaims(claimsCount);
        summary.setPendingClaims(pendingClaimsCount);
        summary.setApprovedClaims(approvedClaimsCount);
        summary.setPotentialMatches(matchesCount);

        summary.setCategoryDistribution(getCategoryDistribution());
        summary.setReportStatusDistribution(getStatusDistribution());
        summary.setMonthlyTrends(getMonthlyTrends());

        return summary;
    }

    public List<CategoryStatDto> getCategoryDistribution() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.match(org.springframework.data.mongodb.core.query.Criteria.where("categoryName").ne(null)),
                Aggregation.group("categoryName").count().as("count"),
                Aggregation.project("count").and("_id").as("categoryName"),
                Aggregation.sort(Sort.Direction.DESC, "count")
        );

        AggregationResults<CategoryStatDto> results = mongoTemplate.aggregate(agg, "item_reports", CategoryStatDto.class);
        return results.getMappedResults();
    }

    public List<StatusStatDto> getStatusDistribution() {
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.group("status").count().as("count"),
                Aggregation.project("count").and("_id").as("status"),
                Aggregation.sort(Sort.Direction.DESC, "count")
        );

        AggregationResults<StatusStatDto> results = mongoTemplate.aggregate(agg, "item_reports", StatusStatDto.class);
        return results.getMappedResults();
    }

    public List<TimelineStatDto> getMonthlyTrends() {
        // Aggregation using Mongo dateToString expression
        Aggregation agg = Aggregation.newAggregation(
                Aggregation.project()
                        .andExpression("dateToString('%Y-%m', createdAt)").as("month")
                        .and("status").as("status"),
                Aggregation.group("month")
                        .count().as("reportsCount"),
                Aggregation.project("reportsCount").and("_id").as("month"),
                Aggregation.sort(Sort.Direction.ASC, "month")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(agg, "item_reports", Document.class);
        List<TimelineStatDto> list = new ArrayList<>();

        for (Document doc : results.getMappedResults()) {
            String month = doc.getString("month");
            Number countNum = (Number) doc.get("reportsCount");
            long count = countNum != null ? countNum.longValue() : 0L;
            list.add(new TimelineStatDto(month != null ? month : "Current", count, 0L));
        }

        return list;
    }
}
