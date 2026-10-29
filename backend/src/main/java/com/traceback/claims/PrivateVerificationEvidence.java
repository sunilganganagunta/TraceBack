package com.traceback.claims;

public class PrivateVerificationEvidence {
    private String identifyingMarks;
    private String serialNumber;
    private String receiptOrProofUrl;
    private String additionalSecretDetails;

    public PrivateVerificationEvidence() {}

    public PrivateVerificationEvidence(String identifyingMarks, String serialNumber,
                                       String receiptOrProofUrl, String additionalSecretDetails) {
        this.identifyingMarks = identifyingMarks;
        this.serialNumber = serialNumber;
        this.receiptOrProofUrl = receiptOrProofUrl;
        this.additionalSecretDetails = additionalSecretDetails;
    }

    public String getIdentifyingMarks() { return identifyingMarks; }
    public void setIdentifyingMarks(String identifyingMarks) { this.identifyingMarks = identifyingMarks; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getReceiptOrProofUrl() { return receiptOrProofUrl; }
    public void setReceiptOrProofUrl(String receiptOrProofUrl) { this.receiptOrProofUrl = receiptOrProofUrl; }

    public String getAdditionalSecretDetails() { return additionalSecretDetails; }
    public void setAdditionalSecretDetails(String additionalSecretDetails) { this.additionalSecretDetails = additionalSecretDetails; }
}
