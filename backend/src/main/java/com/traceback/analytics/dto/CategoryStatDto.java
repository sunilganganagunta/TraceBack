package com.traceback.analytics.dto;

public class CategoryStatDto {
    private String categoryName;
    private long count;

    public CategoryStatDto() {}

    public CategoryStatDto(String categoryName, long count) {
        this.categoryName = categoryName;
        this.count = count;
    }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public long getCount() { return count; }
    public void setCount(long count) { this.count = count; }
}
