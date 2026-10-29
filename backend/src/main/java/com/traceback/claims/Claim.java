package com.traceback.claims;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "claims")
@CompoundIndexes({
        @CompoundIndex(name = "report_claimant_idx", def = "{'reportId': 1, 'claimantId': 1}"),
        @CompoundIndex(name = "status_created_idx", def = "{'status': 1, 'createdAt': -1}")
})
public class Claim {

    @Id
    private String id;

    private String reportId;

    private String reportTitle;

    private String claimantId;

    private String claimantName;

    private String claimDescription;

    private PrivateVerificationEvidence privateVerificationEvidence = new PrivateVerificationEvidence();

    private ClaimStatus status = ClaimStatus.PENDING;

    private String reviewerId;

    private String reviewNotes;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Instant resolvedAt;

    public Claim() {}

    public Claim(String reportId, String reportTitle, String claimantId, String claimantName,
                 String claimDescription, PrivateVerificationEvidence privateVerificationEvidence) {
        this.reportId = reportId;
        this.reportTitle = reportTitle;
        this.claimantId = claimantId;
        this.claimantName = claimantName;
        this.claimDescription = claimDescription;
        this.privateVerificationEvidence = privateVerificationEvidence != null ? privateVerificationEvidence : new PrivateVerificationEvidence();
        this.status = ClaimStatus.PENDING;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getReportTitle() { return reportTitle; }
    public void setReportTitle(String reportTitle) { this.reportTitle = reportTitle; }

    public String getClaimantId() { return claimantId; }
    public void setClaimantId(String claimantId) { this.claimantId = claimantId; }

    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getClaimDescription() { return claimDescription; }
    public void setClaimDescription(String claimDescription) { this.claimDescription = claimDescription; }

    public PrivateVerificationEvidence getPrivateVerificationEvidence() { return privateVerificationEvidence; }
    public void setPrivateVerificationEvidence(PrivateVerificationEvidence privateVerificationEvidence) { this.privateVerificationEvidence = privateVerificationEvidence; }

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }

    public String getReviewNotes() { return reviewNotes; }
    public void setReviewNotes(String reviewNotes) { this.reviewNotes = reviewNotes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
}
