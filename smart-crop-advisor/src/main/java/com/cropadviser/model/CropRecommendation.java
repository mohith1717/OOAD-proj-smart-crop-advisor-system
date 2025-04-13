package com.cropadviser.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CropRecommendation {
    private String cropName;
    private double yield;

    public CropRecommendation() {
        // Default constructor for Jackson
    }

    public CropRecommendation(String cropName, double yield) {
        this.cropName = cropName;
        this.yield = yield;
    }

    @JsonProperty("crop")
    public String getCropName() {
        return cropName;
    }

    @JsonProperty("crop")
    public void setCropName(String cropName) {
        this.cropName = cropName;
    }

    @JsonProperty("yield")
    public double getYield() {
        return yield;
    }

    @JsonProperty("yield")
    public void setYield(double yield) {
        this.yield = yield;
    }
}