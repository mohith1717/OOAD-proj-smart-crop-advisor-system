// src/main/java/com/cropadviser/model/PredictionRequest.java
package com.cropadviser.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

public class PredictionRequest {
    
    @NotBlank(message = "State name is required")
    private String stateName;
    
    @NotBlank(message = "District name is required")
    private String districtName;
    
    @NotBlank(message = "Season name is required")
    private String seasonName;
    
    @Positive(message = "Area must be positive")
    private double area;
    
    // Getters and setters
    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getSeasonName() {
        return seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }
}