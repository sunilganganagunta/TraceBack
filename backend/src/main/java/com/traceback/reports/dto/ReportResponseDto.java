package com.traceback.reports.dto;

import com.traceback.reports.ItemReport;
import com.traceback.reports.ReportStatus;
import com.traceback.reports.ReportType;
import com.traceback.reports.Visibility;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public class ReportResponseDto {

    private String id;
    private String ownerId;
    private String ownerName;
    private ReportType reportType;
    private String title;
    private String categoryId;
    private String categoryName;
    private String description;
    private String color;
    private String brand;
    private String distinguishingFeatures;
    private LocalDate dateLostOrFound;
    private String publicLocationLabel;
    private Double approximateLongitude;
    private Double approximateLatitude;
    private Double exactLongitude; // Only included if owner or authorized reviewer
    private Double exactLatitude;  // Only included if owner or authorized reviewer
    private List<String> images;
    private ReportStatus status;
    private Visibility visibility;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isOwner;

    public ReportResponseDto() {}

    public static ReportResponseDto from(ItemReport report, String currentUserId, boolean isPrivileged) {
        if (report == null) return null;

        ReportResponseDto dto = new ReportResponseDto();
        dto.setId(report.getId());
        dto.setOwnerId(report.getOwnerId());
        dto.setOwnerName(report.getOwnerName());
        dto.setReportType(report.getReportType());
        dto.setTitle(report.getTitle());
        dto.setCategoryId(report.getCategoryId());
        dto.setCategoryName(report.getCategoryName());
        dto.setDescription(report.getDescription());
        dto.setColor(report.getColor());
        dto.setBrand(report.getBrand());
        dto.setDistinguishingFeatures(report.getDistinguishingFeatures());
        dto.setDateLostOrFound(report.getDateLostOrFound());
        dto.setPublicLocationLabel(report.getPublicLocationLabel());
        dto.setImages(report.getImages());
        dto.setStatus(report.getStatus());
        dto.setVisibility(report.getVisibility());
        dto.setCreatedAt(report.getCreatedAt());
        dto.setUpdatedAt(report.getUpdatedAt());

        boolean userIsOwner = currentUserId != null && currentUserId.equals(report.getOwnerId());
        dto.setOwner(userIsOwner);

        if (report.getLocation() != null) {
            double lng = report.getLocation().getX();
            double lat = report.getLocation().getY();

            // Approximate coordinates: rounded to 2 decimal places for public privacy (~1 km)
            dto.setApproximateLongitude(Math.round(lng * 100.0) / 100.0);
            dto.setApproximateLatitude(Math.round(lat * 100.0) / 100.0);

            // Exact coordinates only exposed to owner or privileged moderator/admin
            if (userIsOwner || isPrivileged) {
                dto.setExactLongitude(lng);
                dto.setExactLatitude(lat);
            }
        }

        return dto;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getDistinguishingFeatures() { return distinguishingFeatures; }
    public void setDistinguishingFeatures(String distinguishingFeatures) { this.distinguishingFeatures = distinguishingFeatures; }

    public LocalDate getDateLostOrFound() { return dateLostOrFound; }
    public void setDateLostOrFound(LocalDate dateLostOrFound) { this.dateLostOrFound = dateLostOrFound; }

    public String getPublicLocationLabel() { return publicLocationLabel; }
    public void setPublicLocationLabel(String publicLocationLabel) { this.publicLocationLabel = publicLocationLabel; }

    public Double getApproximateLongitude() { return approximateLongitude; }
    public void setApproximateLongitude(Double approximateLongitude) { this.approximateLongitude = approximateLongitude; }

    public Double getApproximateLatitude() { return approximateLatitude; }
    public void setApproximateLatitude(Double approximateLatitude) { this.approximateLatitude = approximateLatitude; }

    public Double getExactLongitude() { return exactLongitude; }
    public void setExactLongitude(Double exactLongitude) { this.exactLongitude = exactLongitude; }

    public Double getExactLatitude() { return exactLatitude; }
    public void setExactLatitude(Double exactLatitude) { this.exactLatitude = exactLatitude; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public boolean isOwner() { return isOwner; }
    public void setOwner(boolean owner) { isOwner = owner; }
}
