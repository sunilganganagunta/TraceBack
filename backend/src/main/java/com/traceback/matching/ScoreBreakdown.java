package com.traceback.matching;

public class ScoreBreakdown {
    private double categoryScore;
    private double titleScore;
    private double descriptionScore;
    private double attributeScore;
    private double dateScore;
    private double proximityScore;

    public ScoreBreakdown() {}

    public ScoreBreakdown(double categoryScore, double titleScore, double descriptionScore,
                          double attributeScore, double dateScore, double proximityScore) {
        this.categoryScore = categoryScore;
        this.titleScore = titleScore;
        this.descriptionScore = descriptionScore;
        this.attributeScore = attributeScore;
        this.dateScore = dateScore;
        this.proximityScore = proximityScore;
    }

    public double getCategoryScore() { return categoryScore; }
    public void setCategoryScore(double categoryScore) { this.categoryScore = categoryScore; }

    public double getTitleScore() { return titleScore; }
    public void setTitleScore(double titleScore) { this.titleScore = titleScore; }

    public double getDescriptionScore() { return descriptionScore; }
    public void setDescriptionScore(double descriptionScore) { this.descriptionScore = descriptionScore; }

    public double getAttributeScore() { return attributeScore; }
    public void setAttributeScore(double attributeScore) { this.attributeScore = attributeScore; }

    public double getDateScore() { return dateScore; }
    public void setDateScore(double dateScore) { this.dateScore = dateScore; }

    public double getProximityScore() { return proximityScore; }
    public void setProximityScore(double proximityScore) { this.proximityScore = proximityScore; }

    public double getTotal() {
        return categoryScore + titleScore + descriptionScore + attributeScore + dateScore + proximityScore;
    }
}
