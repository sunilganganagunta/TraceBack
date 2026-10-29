package com.traceback.claims.dto;

import com.traceback.claims.ClaimStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewClaimRequest {

    @NotNull(message = "Status is required")
    private ClaimStatus status;

    @Size(max = 1000, message = "Review notes must not exceed 1000 characters")
    private String reviewNotes;

    public ReviewClaimRequest() {}

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public String getReviewNotes() { return reviewNotes; }
    public void setReviewNotes(String reviewNotes) { this.reviewNotes = reviewNotes; }
}
