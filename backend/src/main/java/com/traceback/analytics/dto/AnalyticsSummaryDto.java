package com.traceback.analytics.dto;

import java.util.List;

public class AnalyticsSummaryDto {
    private long totalUsers;
    private long totalReports;
    private long totalLostReports;
    private long totalFoundReports;
    private long activeReports;
    private long recoveredItems;
    private double recoveryRate;
    private long totalClaims;
    private long pendingClaims;
    private long approvedClaims;
    private long potentialMatches;
    private List<CategoryStatDto> categoryDistribution;
    private List<StatusStatDto> reportStatusDistribution;
    private List<TimelineStatDto> monthlyTrends;

    public AnalyticsSummaryDto() {}

    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }

    public long getTotalReports() { return totalReports; }
    public void setTotalReports(long totalReports) { this.totalReports = totalReports; }

    public long getTotalLostReports() { return totalLostReports; }
    public void setTotalLostReports(long totalLostReports) { this.totalLostReports = totalLostReports; }

    public long getTotalFoundReports() { return totalFoundReports; }
    public void setTotalFoundReports(long totalFoundReports) { this.totalFoundReports = totalFoundReports; }

    public long getActiveReports() { return activeReports; }
    public void setActiveReports(long activeReports) { this.activeReports = activeReports; }

    public long getRecoveredItems() { return recoveredItems; }
    public void setRecoveredItems(long recoveredItems) { this.recoveredItems = recoveredItems; }

    public double getRecoveryRate() { return recoveryRate; }
    public void setRecoveryRate(double recoveryRate) { this.recoveryRate = recoveryRate; }

    public long getTotalClaims() { return totalClaims; }
    public void setTotalClaims(long totalClaims) { this.totalClaims = totalClaims; }

    public long getPendingClaims() { return pendingClaims; }
    public void setPendingClaims(long pendingClaims) { this.pendingClaims = pendingClaims; }

    public long getApprovedClaims() { return approvedClaims; }
    public void setApprovedClaims(long approvedClaims) { this.approvedClaims = approvedClaims; }

    public long getPotentialMatches() { return potentialMatches; }
    public void setPotentialMatches(long potentialMatches) { this.potentialMatches = potentialMatches; }

    public List<CategoryStatDto> getCategoryDistribution() { return categoryDistribution; }
    public void setCategoryDistribution(List<CategoryStatDto> categoryDistribution) { this.categoryDistribution = categoryDistribution; }

    public List<StatusStatDto> getReportStatusDistribution() { return reportStatusDistribution; }
    public void setReportStatusDistribution(List<StatusStatDto> reportStatusDistribution) { this.reportStatusDistribution = reportStatusDistribution; }

    public List<TimelineStatDto> getMonthlyTrends() { return monthlyTrends; }
    public void setMonthlyTrends(List<TimelineStatDto> monthlyTrends) { this.monthlyTrends = monthlyTrends; }
}
