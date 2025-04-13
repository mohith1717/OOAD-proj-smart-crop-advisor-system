package com.cropadviser.service;

import com.cropadviser.model.CropRecommendation;
import com.cropadviser.model.PredictionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CropRecommendationService {

    @Autowired
    private PredictionApiService predictionApiService;

    public List<CropRecommendation> recommendCrops(PredictionRequest request) {
        return predictionApiService.predictCrops(request);
    }
}