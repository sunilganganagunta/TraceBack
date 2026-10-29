package com.traceback.claims.dto;

import com.traceback.claims.PrivateVerificationEvidence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateClaimRequest {

    @NotBlank(message = "Report ID is required")
    private String reportId;

    @NotBlank(message = "Claim description is required")
    @Size(min = 10, max = 1000, message = "Claim description must be between 10 and 1000 characters")
    private String claimDescription;

    private PrivateVerificationEvidence privateEvidence;

    public CreateClaimRequest() {}

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }

    public String getClaimDescription() { return claimDescription; }
    public void setClaimDescription(String claimDescription) { this.claimDescription = claimDescription; }

    public PrivateVerificationEvidence getPrivateEvidence() { return privateEvidence; }
    public void setPrivateEvidence(PrivateVerificationEvidence privateEvidence) { this.privateEvidence = privateEvidence; }
}
