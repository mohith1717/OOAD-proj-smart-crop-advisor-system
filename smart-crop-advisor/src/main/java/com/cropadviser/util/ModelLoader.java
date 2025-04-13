package com.cropadviser.util;

import com.cropadviser.model.CropRecommendation;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class ModelLoader {
    
    private String modelPath;
    private String scriptPath;
    private boolean isPythonAvailable = false;
    
    public ModelLoader() {
        try {
            // Check if Python is available
            Process process = Runtime.getRuntime().exec("python --version");
            int exitCode = process.waitFor();
            isPythonAvailable = (exitCode == 0);
            
            if (!isPythonAvailable) {
                System.err.println("Python is not available. Using fallback recommendations.");
                return;
            }
            
            // Get paths to model and script
            File modelFile = new ClassPathResource("model/crop_recommendation_model.pkl").getFile();
            File scriptFile = new ClassPathResource("python/predict.py").getFile();
            
            modelPath = modelFile.getAbsolutePath();
            scriptPath = scriptFile.getAbsolutePath();
            
            System.out.println("Model path: " + modelPath);
            System.out.println("Script path: " + scriptPath);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error initializing ModelLoader: " + e.getMessage());
        }
    }
    
    public List<CropRecommendation> predictCrops(String state, String district, String season, double area) {
        if (!isPythonAvailable) {
            return getFallbackRecommendations(state);
        }
        
        try {
            // Build command to run Python script
            ProcessBuilder pb = new ProcessBuilder(
                "python", scriptPath, modelPath, state, district, season, String.valueOf(area)
            );
            
            // Don't redirect stderr to stdout
            pb.redirectErrorStream(false);
            
            // Start process
            Process process = pb.start();
            
            // Read stdout (should be just JSON)
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder jsonOutput = new StringBuilder();
            String line;
            while ((line = stdoutReader.readLine()) != null) {
                jsonOutput.append(line);
            }
            
            // Read stderr (for debugging)
            BufferedReader stderrReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            StringBuilder errorOutput = new StringBuilder();
            while ((line = stderrReader.readLine()) != null) {
                errorOutput.append(line).append("\n");
            }
            
            // Wait for process to complete
            int exitCode = process.waitFor();
            
            // Log stderr output if any
            if (errorOutput.length() > 0) {
                System.err.println("Python stderr output: " + errorOutput.toString());
            }
            
            if (exitCode != 0) {
                System.err.println("Python script execution failed with exit code: " + exitCode);
                return getFallbackRecommendations(state);
            }
            
            // Parse JSON result
            String jsonString = jsonOutput.toString().trim();
            System.out.println("Python script JSON output: " + jsonString);
            
            // Only try to parse if we have valid JSON
            if (jsonString.startsWith("[") && jsonString.endsWith("]")) {
                ObjectMapper mapper = new ObjectMapper();
                List<CropRecommendation> recommendations = mapper.readValue(
                    jsonString, 
                    new TypeReference<List<CropRecommendation>>() {}
                );
                return recommendations;
            } else {
                System.err.println("Invalid JSON returned from Python script");
                return getFallbackRecommendations(state);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return getFallbackRecommendations(state);
        }
    }
    
    private List<CropRecommendation> getFallbackRecommendations(String state) {
        // Generate state and crop specific recommendations as fallback
        List<CropRecommendation> fallback = new ArrayList<>();
        String stateLower = state.toLowerCase();
        
        // Somewhat realistic recommendations based on state
        if (stateLower.contains("assam")) {
            fallback.add(new CropRecommendation("Rice", 4.7));
            fallback.add(new CropRecommendation("Tea", 6.2));
            fallback.add(new CropRecommendation("Jute", 3.9));
            fallback.add(new CropRecommendation("Maize", 2.8));
            fallback.add(new CropRecommendation("Sugarcane", 5.5));
        } else if (stateLower.contains("punjab")) {
            fallback.add(new CropRecommendation("Wheat", 5.2));
            fallback.add(new CropRecommendation("Rice", 4.8));
            fallback.add(new CropRecommendation("Cotton", 3.1));
            fallback.add(new CropRecommendation("Maize", 4.0));
            fallback.add(new CropRecommendation("Sugarcane", 6.3));
        } else {
            // Generic fallback
            fallback.add(new CropRecommendation("Rice", 4.5));
            fallback.add(new CropRecommendation("Wheat", 3.8));
            fallback.add(new CropRecommendation("Maize", 3.2));
            fallback.add(new CropRecommendation("Cotton", 2.9));
            fallback.add(new CropRecommendation("Sugarcane", 5.2));
        }
        
        return fallback;
    }
}