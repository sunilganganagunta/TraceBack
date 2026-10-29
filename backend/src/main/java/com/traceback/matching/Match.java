package com.traceback.matching;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "matches")
@CompoundIndexes({
        @CompoundIndex(name = "unique_report_pair_idx", def = "{'lostReportId': 1, 'foundReportId': 1}", unique = true),
        @CompoundIndex(name = "lost_report_score_idx", def = "{'lostReportId': 1, 'score': -1}"),
        @CompoundIndex(name = "found_report_score_idx", def = "{'foundReportId': 1, 'score': -1}")
})
public class Match {

    @Id
    private String id;

    private String lostReportId;

    private String foundReportId;

    private double score;

    private ScoreBreakdown scoreBreakdown = new ScoreBreakdown();

    private List<String> reasons = new ArrayList<>();

    private MatchStatus status = MatchStatus.ACTIVE;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Instant lastCalculatedAt;

    public Match() {}

    public Match(String lostReportId, String foundReportId, double score, ScoreBreakdown scoreBreakdown, List<String> reasons) {
        this.lostReportId = lostReportId;
        this.foundReportId = foundReportId;
        this.score = score;
        this.scoreBreakdown = scoreBreakdown;
        this.reasons = reasons != null ? reasons : new ArrayList<>();
        this.status = MatchStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastCalculatedAt = Instant.now();
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

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getLastCalculatedAt() { return lastCalculatedAt; }
    public void setLastCalculatedAt(Instant lastCalculatedAt) { this.lastCalculatedAt = lastCalculatedAt; }
}
