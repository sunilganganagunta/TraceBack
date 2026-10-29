package com.traceback.matching;

import com.traceback.common.ForbiddenException;
import com.traceback.common.ResourceNotFoundException;
import com.traceback.matching.dto.MatchResponseDto;
import com.traceback.reports.ItemReport;
import com.traceback.reports.ItemReportRepository;
import com.traceback.reports.ReportType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final ItemReportRepository reportRepository;
    private final MatchingEngineService matchingEngineService;

    public MatchService(MatchRepository matchRepository,
                        ItemReportRepository reportRepository,
                        MatchingEngineService matchingEngineService) {
        this.matchRepository = matchRepository;
        this.reportRepository = reportRepository;
        this.matchingEngineService = matchingEngineService;
    }

    public List<MatchResponseDto> getMatchesForReport(String reportId, String userId, boolean isPrivileged) {
        ItemReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("ItemReport", "id", reportId));

        if (!report.getOwnerId().equals(userId) && !isPrivileged) {
            throw new ForbiddenException("You can only view matches for your own reports");
        }

        List<Match> matches = (report.getReportType() == ReportType.LOST)
                ? matchRepository.findByLostReportIdOrderByScoreDesc(reportId)
                : matchRepository.findByFoundReportIdOrderByScoreDesc(reportId);

        return enrichMatches(matches);
    }

    public List<MatchResponseDto> getMyMatches(String userId) {
        List<ItemReport> myReports = reportRepository.findByOwnerId(userId);
        if (myReports.isEmpty()) return List.of();

        List<String> lostIds = myReports.stream()
                .filter(r -> r.getReportType() == ReportType.LOST)
                .map(ItemReport::getId)
                .toList();

        List<String> foundIds = myReports.stream()
                .filter(r -> r.getReportType() == ReportType.FOUND)
                .map(ItemReport::getId)
                .toList();

        List<Match> allMatches = new ArrayList<>();
        for (String id : lostIds) {
            allMatches.addAll(matchRepository.findByLostReportIdOrderByScoreDesc(id));
        }
        for (String id : foundIds) {
            allMatches.addAll(matchRepository.findByFoundReportIdOrderByScoreDesc(id));
        }

        // Sort by score descending and take top 50
        List<Match> sorted = allMatches.stream()
                .distinct()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(50)
                .toList();

        return enrichMatches(sorted);
    }

    public List<MatchResponseDto> recalculateMatches(String reportId, String userId, boolean isPrivileged) {
        ItemReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("ItemReport", "id", reportId));

        if (!report.getOwnerId().equals(userId) && !isPrivileged) {
            throw new ForbiddenException("You do not have permission to recalculate matches for this report");
        }

        List<Match> calculated = matchingEngineService.calculateMatchesForReport(report);
        return enrichMatches(calculated);
    }

    private List<MatchResponseDto> enrichMatches(List<Match> matches) {
        if (matches.isEmpty()) return List.of();

        List<String> allReportIds = new ArrayList<>();
        for (Match m : matches) {
            allReportIds.add(m.getLostReportId());
            allReportIds.add(m.getFoundReportId());
        }

        Map<String, ItemReport> reportMap = reportRepository.findAllById(allReportIds).stream()
                .collect(Collectors.toMap(ItemReport::getId, r -> r));

        return matches.stream()
                .map(m -> MatchResponseDto.from(m, reportMap.get(m.getLostReportId()), reportMap.get(m.getFoundReportId())))
                .toList();
    }
}
