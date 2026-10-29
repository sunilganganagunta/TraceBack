package com.traceback.claims.dto;

import com.traceback.claims.Claim;
import com.traceback.claims.ClaimStatus;
import com.traceback.claims.PrivateVerificationEvidence;

import java.time.Instant;

public class ClaimResponseDto {

    private String id;
    private String reportId;
    private String reportTitle;
    private String claimantId;
    private String claimantName;
    private String claimDescription;
    private PrivateVerificationEvidence privateVerificationEvidence;
    private ClaimStatus status;
    private String reviewerId;
    private String reviewNotes;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant resolvedAt;
    private boolean isClaimant;

    public ClaimResponseDto() {}

    public static ClaimResponseDto from(Claim claim, String currentUserId, boolean isOwnerOrPrivileged) {
        if (claim == null) return null;

        ClaimResponseDto dto = new ClaimResponseDto();
        dto.setId(claim.getId());
        dto.setReportId(claim.getReportId());
        dto.setReportTitle(claim.getReportTitle());
        dto.setClaimantId(claim.getClaimantId());
        dto.setClaimantName(claim.getClaimantName());
        dto.setClaimDescription(claim.getClaimDescription());
        dto.setStatus(claim.getStatus());
        dto.setReviewerId(claim.getReviewerId());
        dto.setReviewNotes(claim.getReviewNotes());
        dto.setCreatedAt(claim.getCreatedAt());
        dto.setUpdatedAt(claim.getUpdatedAt());
        dto.setResolvedAt(claim.getResolvedAt());

        boolean userIsClaimant = currentUserId != null && currentUserId.equals(claim.getClaimantId());
        dto.setClaimant(userIsClaimant);

        // Security rule: Only the claimant, the report owner, or a moderator/admin can inspect private evidence
        if (userIsClaimant || isOwnerOrPrivileged) {
            dto.setPrivateVerificationEvidence(claim.getPrivateVerificationEvidence());
        }

        return dto;
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

    public boolean isClaimant() { return isClaimant; }
    public void setClaimant(boolean claimant) { isClaimant = claimant; }
}
