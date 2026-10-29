package com.traceback.matching;

import com.traceback.reports.ItemReport;
import com.traceback.reports.ItemReportRepository;
import com.traceback.reports.ReportStatus;
import com.traceback.reports.ReportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingEngineService {

    private static final Logger log = LoggerFactory.getLogger(MatchingEngineService.class);
    private static final double MIN_MATCH_SCORE_THRESHOLD = 30.0;

    private final MatchRepository matchRepository;
    private final ItemReportRepository reportRepository;

    public MatchingEngineService(MatchRepository matchRepository, ItemReportRepository reportRepository) {
        this.matchRepository = matchRepository;
        this.reportRepository = reportRepository;
    }

    /**
     * Compute matches for a newly created or updated report against opposing active candidate reports.
     */
    public List<Match> calculateMatchesForReport(ItemReport sourceReport) {
        if (sourceReport.getStatus() == ReportStatus.WITHDRAWN || sourceReport.getStatus() == ReportStatus.CLOSED) {
            return Collections.emptyList();
        }

        ReportType targetType = (sourceReport.getReportType() == ReportType.LOST)
                ? ReportType.FOUND
                : ReportType.LOST;

        // Query candidate reports of the opposing type that are active or matched
        List<ItemReport> candidates = reportRepository.findAll().stream()
                .filter(r -> r.getReportType() == targetType)
                .filter(r -> r.getStatus() == ReportStatus.ACTIVE || r.getStatus() == ReportStatus.MATCHED)
                .filter(r -> !r.getOwnerId().equals(sourceReport.getOwnerId())) // Don't match user's own items
                .toList();

        List<Match> generatedMatches = new ArrayList<>();

        for (ItemReport candidate : candidates) {
            ItemReport lostReport = (sourceReport.getReportType() == ReportType.LOST) ? sourceReport : candidate;
            ItemReport foundReport = (sourceReport.getReportType() == ReportType.LOST) ? candidate : sourceReport;

            Match match = scoreReportPair(lostReport, foundReport);
            if (match.getScore() >= MIN_MATCH_SCORE_THRESHOLD) {
                // Upsert match pair to prevent duplicates
                Optional<Match> existingOpt = matchRepository.findByLostReportIdAndFoundReportId(
                        lostReport.getId(), foundReport.getId());

                if (existingOpt.isPresent()) {
                    Match existing = existingOpt.get();
                    existing.setScore(match.getScore());
                    existing.setScoreBreakdown(match.getScoreBreakdown());
                    existing.setReasons(match.getReasons());
                    existing.setLastCalculatedAt(Instant.now());
                    existing.setUpdatedAt(Instant.now());
                    generatedMatches.add(matchRepository.save(existing));
                } else {
                    generatedMatches.add(matchRepository.save(match));
                }
            }
        }

        log.info("Calculated {} candidate matches for report {}", generatedMatches.size(), sourceReport.getId());
        return generatedMatches;
    }

    /**
     * Evaluates similarity between a LOST and a FOUND report deterministically.
     */
    public Match scoreReportPair(ItemReport lost, ItemReport found) {
        double categoryScore = 0.0;
        double titleScore = 0.0;
        double descriptionScore = 0.0;
        double attributeScore = 0.0;
        double dateScore = 0.0;
        double proximityScore = 0.0;

        List<String> reasons = new ArrayList<>();

        // 1. Category Matching (Max 30)
        if (lost.getCategoryId() != null && lost.getCategoryId().equals(found.getCategoryId())) {
            categoryScore = 30.0;
            reasons.add("Category matches: " + (lost.getCategoryName() != null ? lost.getCategoryName() : "Same Category"));
        }

        // 2. Title Keyword Overlap (Max 25)
        Set<String> lostTitleTokens = tokenize(lost.getTitle());
        Set<String> foundTitleTokens = tokenize(found.getTitle());
        double titleOverlap = calculateJaccardSimilarity(lostTitleTokens, foundTitleTokens);
        if (titleOverlap > 0) {
            titleScore = Math.min(25.0, titleOverlap * 25.0 * 1.5);
            Set<String> commonTokens = new HashSet<>(lostTitleTokens);
            commonTokens.retainAll(foundTitleTokens);
            if (!commonTokens.isEmpty()) {
                reasons.add("Title matches keywords: " + String.join(", ", commonTokens));
            }
        }

        // 3. Description Keywords (Max 15)
        Set<String> lostDescTokens = tokenize(lost.getDescription());
        Set<String> foundDescTokens = tokenize(found.getDescription());
        double descOverlap = calculateJaccardSimilarity(lostDescTokens, foundDescTokens);
        if (descOverlap > 0.1) {
            descriptionScore = Math.min(15.0, descOverlap * 20.0);
            reasons.add("Descriptions share contextual terms");
        }

        // 4. Color & Brand Attributes (Max 10)
        if (lost.getColor() != null && found.getColor() != null &&
                lost.getColor().trim().equalsIgnoreCase(found.getColor().trim())) {
            attributeScore += 5.0;
            reasons.add("Color matches: " + lost.getColor().trim());
        }

        if (lost.getBrand() != null && found.getBrand() != null && !lost.getBrand().isBlank() &&
                lost.getBrand().trim().equalsIgnoreCase(found.getBrand().trim())) {
            attributeScore += 5.0;
            reasons.add("Brand matches: " + lost.getBrand().trim());
        }

        // 5. Date Compatibility (Max 10)
        if (lost.getDateLostOrFound() != null && found.getDateLostOrFound() != null) {
            long daysBetween = ChronoUnit.DAYS.between(lost.getDateLostOrFound(), found.getDateLostOrFound());
            if (daysBetween >= 0 && daysBetween <= 7) {
                dateScore = 10.0;
                reasons.add("Dates compatible (Found within 7 days of loss)");
            } else if (daysBetween >= 0 && daysBetween <= 30) {
                dateScore = 6.0;
                reasons.add("Dates compatible (Found within 30 days of loss)");
            } else if (daysBetween < 0 && daysBetween >= -2) {
                // Small reporting timezone / recollection margin
                dateScore = 3.0;
                reasons.add("Dates approximately compatible");
            }
        }

        // 6. Geospatial Proximity (Max 15)
        if (lost.getLocation() != null && found.getLocation() != null) {
            double distanceKm = calculateHaversineDistanceKm(
                    lost.getLocation().getY(), lost.getLocation().getX(),
                    found.getLocation().getY(), found.getLocation().getX()
            );

            if (distanceKm <= 0.5) { // 500m
                proximityScore = 15.0;
                reasons.add(String.format("Location proximity: Within %d meters", Math.round(distanceKm * 1000)));
            } else if (distanceKm <= 2.0) { // 2km
                proximityScore = 10.0;
                reasons.add(String.format("Location proximity: Within %.1f km", distanceKm));
            } else if (distanceKm <= 10.0) { // 10km
                proximityScore = 5.0;
                reasons.add(String.format("Location proximity: In vicinity (%.1f km)", distanceKm));
            }
        }

        double totalScore = Math.min(100.0, categoryScore + titleScore + descriptionScore + attributeScore + dateScore + proximityScore);
        totalScore = Math.round(totalScore * 10.0) / 10.0;

        ScoreBreakdown breakdown = new ScoreBreakdown(
                categoryScore, titleScore, descriptionScore, attributeScore, dateScore, proximityScore
        );

        return new Match(lost.getId(), found.getId(), totalScore, breakdown, reasons);
    }

    private Set<String> tokenize(String text) {
        if (text == null) return Collections.emptySet();
        return Arrays.stream(text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").split("\\s+"))
                .map(String::trim)
                .filter(s -> s.length() > 2)
                .filter(s -> !STOP_WORDS.contains(s))
                .collect(Collectors.toSet());
    }

    private double calculateJaccardSimilarity(Set<String> set1, Set<String> set2) {
        if (set1.isEmpty() || set2.isEmpty()) return 0.0;
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        Set<String> union = new HashSet<>(set1);
        union.addAll(set2);
        return (double) intersection.size() / union.size();
    }

    private double calculateHaversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "and", "a", "an", "in", "on", "at", "to", "for", "with", "from",
            "item", "lost", "found", "near", "please", "help", "this", "that"
    );
}
