package com.traceback.reports.dto;

import com.traceback.reports.ReportStatus;
import com.traceback.reports.Visibility;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateReportRequest {

    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;

    private String categoryId;

    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;

    private String color;

    private String brand;

    private String distinguishingFeatures;

    private String publicLocationLabel;

    private Double longitude;

    private Double latitude;

    private List<String> images;

    private ReportStatus status;

    private Visibility visibility;

    public UpdateReportRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getDistinguishingFeatures() { return distinguishingFeatures; }
    public void setDistinguishingFeatures(String distinguishingFeatures) { this.distinguishingFeatures = distinguishingFeatures; }

    public String getPublicLocationLabel() { return publicLocationLabel; }
    public void setPublicLocationLabel(String publicLocationLabel) { this.publicLocationLabel = publicLocationLabel; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }
}
