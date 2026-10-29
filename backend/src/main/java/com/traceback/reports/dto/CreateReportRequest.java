package com.traceback.reports.dto;

import com.traceback.reports.ReportType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public class CreateReportRequest {

    @NotNull(message = "Report type (LOST or FOUND) is required")
    private ReportType reportType;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 120, message = "Title must be between 3 and 120 characters")
    private String title;

    @NotBlank(message = "Category ID is required")
    private String categoryId;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    private String description;

    @NotBlank(message = "Color is required")
    private String color;

    private String brand;

    private String distinguishingFeatures;

    @NotNull(message = "Date lost or found is required")
    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate dateLostOrFound;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    private Double longitude;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    private Double latitude;

    @NotBlank(message = "Public location label is required")
    @Size(max = 200, message = "Location label must not exceed 200 characters")
    private String publicLocationLabel;

    private List<String> images;

    public CreateReportRequest() {}

    public ReportType getReportType() { return reportType; }
    public void setReportType(ReportType reportType) { this.reportType = reportType; }

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

    public LocalDate getDateLostOrFound() { return dateLostOrFound; }
    public void setDateLostOrFound(LocalDate dateLostOrFound) { this.dateLostOrFound = dateLostOrFound; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public String getPublicLocationLabel() { return publicLocationLabel; }
    public void setPublicLocationLabel(String publicLocationLabel) { this.publicLocationLabel = publicLocationLabel; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
}
