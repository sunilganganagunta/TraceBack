package com.traceback.analytics.dto;

public class TimelineStatDto {
    private String month;
    private long reportsCount;
    private long recoveriesCount;

    public TimelineStatDto() {}

    public TimelineStatDto(String month, long reportsCount, long recoveriesCount) {
        this.month = month;
        this.reportsCount = reportsCount;
        this.recoveriesCount = recoveriesCount;
    }

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public long getReportsCount() { return reportsCount; }
    public void setReportsCount(long reportsCount) { this.reportsCount = reportsCount; }

    public long getRecoveriesCount() { return recoveriesCount; }
    public void setRecoveriesCount(long recoveriesCount) { this.recoveriesCount = recoveriesCount; }
}
