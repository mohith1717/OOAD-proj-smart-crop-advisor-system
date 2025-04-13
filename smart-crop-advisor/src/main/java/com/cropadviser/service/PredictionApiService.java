package com.cropadviser.service;

import com.cropadviser.model.CropRecommendation;
import com.cropadviser.model.PredictionRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PredictionApiService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Value("${prediction.api.url:http://localhost:8080/predict}")
    private String apiUrl;
    
    public List<CropRecommendation> predictCrops(PredictionRequest request) {
        try {
            // Prepare request body with trimmed values
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("state", request.getStateName().trim());
            requestBody.put("district", request.getDistrictName().trim());
            requestBody.put("season", request.getSeasonName().trim());
            requestBody.put("area", request.getArea());
            requestBody.put("topN", 5);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create http entity
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            // Log the request
            System.out.println("Making API request to: " + apiUrl);
            System.out.println("Request body: " + objectMapper.writeValueAsString(requestBody));
            
            // Make API call
            ResponseEntity<String> response = 
                restTemplate.postForEntity(apiUrl, entity, String.class);
            
            // Parse response
            if (response.getStatusCode().is2xxSuccessful()) {
                String responseBody = response.getBody();
                System.out.println("API response: " + responseBody);
                
                List<CropRecommendation> recommendations = objectMapper.readValue(
                    responseBody, 
                    new TypeReference<List<CropRecommendation>>() {}
                );
                return recommendations;
            } else {
                System.err.println("API request failed with status: " + response.getStatusCodeValue());
                return getFallbackRecommendations(request.getStateName());
            }
        } catch (Exception e) {
            System.err.println("Error calling prediction API: " + e.getMessage());
            e.printStackTrace();
            return getFallbackRecommendations(request.getStateName());
        }
    }
    
    private List<CropRecommendation> getFallbackRecommendations(String state) {
        // Generate state and crop specific recommendations as fallback
        List<CropRecommendation> fallback = new ArrayList<>();
        String stateLower = state.toLowerCase();
        
        // Somewhat realistic recommendations based on state
        if (stateLower.contains("karnataka")) {
            fallback.add(new CropRecommendation("Rice", 3.5));
            fallback.add(new CropRecommendation("Maize", 3.2));
            fallback.add(new CropRecommendation("Ragi", 2.8));
            fallback.add(new CropRecommendation("Jowar", 2.5));
            fallback.add(new CropRecommendation("Groundnut", 1.8));
        } else if (stateLower.contains("assam")) {
            fallback.add(new CropRecommendation("Rice", 4.7));
            fallback.add(new CropRecommendation("Tea", 6.2));
            fallback.add(new CropRecommendation("Jute", 3.9));
            fallback.add(new CropRecommendation("Maize", 2.8));
            fallback.add(new CropRecommendation("Sugarcane", 5.5));
        } else {
            // Generic fallback
            fallback.add(new CropRecommendation("Rice", 3.0));
            fallback.add(new CropRecommendation("Wheat", 2.8));
            fallback.add(new CropRecommendation("Maize", 2.7));
            fallback.add(new CropRecommendation("Cotton", 2.2));
            fallback.add(new CropRecommendation("Soybean", 1.9));
        }
        
        return fallback;
    }
}