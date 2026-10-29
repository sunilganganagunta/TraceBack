package com.traceback.matching.dto;

import com.traceback.matching.Match;
import com.traceback.matching.MatchStatus;
import com.traceback.matching.ScoreBreakdown;
import com.traceback.reports.ItemReport;

import java.time.Instant;
import java.util.List;

public class MatchResponseDto {

    private String id;
    private String lostReportId;
    private String foundReportId;
    private double score;
    private ScoreBreakdown scoreBreakdown;
    private List<String> reasons;
    private MatchStatus status;
    private Instant createdAt;

    private ItemSummary lostReport;
    private ItemSummary foundReport;

    public MatchResponseDto() {}

    public static MatchResponseDto from(Match match, ItemReport lost, ItemReport found) {
        if (match == null) return null;

        MatchResponseDto dto = new MatchResponseDto();
        dto.setId(match.getId());
        dto.setLostReportId(match.getLostReportId());
        dto.setFoundReportId(match.getFoundReportId());
        dto.setScore(match.getScore());
        dto.setScoreBreakdown(match.getScoreBreakdown());
        dto.setReasons(match.getReasons());
        dto.setStatus(match.getStatus());
        dto.setCreatedAt(match.getCreatedAt());

        if (lost != null) {
            dto.setLostReport(new ItemSummary(
                    lost.getId(), lost.getTitle(), lost.getCategoryName(), lost.getColor(),
                    lost.getBrand(), lost.getPublicLocationLabel(),
                    lost.getImages() != null && !lost.getImages().isEmpty() ? lost.getImages().get(0) : null
            ));
        }

        if (found != null) {
            dto.setFoundReport(new ItemSummary(
                    found.getId(), found.getTitle(), found.getCategoryName(), found.getColor(),
                    found.getBrand(), found.getPublicLocationLabel(),
                    found.getImages() != null && !found.getImages().isEmpty() ? found.getImages().get(0) : null
            ));
        }

        return dto;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLostReportId() { return lostReportId; }
    public void setLostReportId(String lostReportId) { this.lostReportId = lostReportId; }

    public String getFoundReportId() { return foundReportId; }
    public void setFoundReportId(String foundReportId) { this.foundReportId = foundReportId; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }

    public ScoreBreakdown getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(ScoreBreakdown scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }

    public List<String> getReasons() { return reasons; }
    public void setReasons(List<String> reasons) { this.reasons = reasons; }

    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public ItemSummary getLostReport() { return lostReport; }
    public void setLostReport(ItemSummary lostReport) { this.lostReport = lostReport; }

    public ItemSummary getFoundReport() { return foundReport; }
    public void setFoundReport(ItemSummary foundReport) { this.foundReport = foundReport; }

    public static class ItemSummary {
        private String id;
        private String title;
        private String categoryName;
        private String color;
        private String brand;
        private String locationLabel;
        private String image;

        public ItemSummary() {}

        public ItemSummary(String id, String title, String categoryName, String color, String brand, String locationLabel, String image) {
            this.id = id;
            this.title = title;
            this.categoryName = categoryName;
            this.color = color;
            this.brand = brand;
            this.locationLabel = locationLabel;
            this.image = image;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getCategoryName() { return categoryName; }
        public String getColor() { return color; }
        public String getBrand() { return brand; }
        public String getLocationLabel() { return locationLabel; }
        public String getImage() { return image; }
    }
}
