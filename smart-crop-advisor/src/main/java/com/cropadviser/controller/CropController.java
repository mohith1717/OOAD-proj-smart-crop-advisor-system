package com.cropadviser.controller;

import com.cropadviser.model.CropRecommendation;
import com.cropadviser.model.PredictionRequest;
import com.cropadviser.service.CropRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
public class CropController {

    @Autowired
    private CropRecommendationService cropRecommendationService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("predictionRequest", new PredictionRequest());
        return "index";
    }

    @PostMapping("/recommend")
    public String recommendCrops(@Valid @ModelAttribute PredictionRequest predictionRequest, 
                                BindingResult result, 
                                Model model) {
        if (result.hasErrors()) {
            return "index";
        }
        
        List<CropRecommendation> recommendations = cropRecommendationService.recommendCrops(predictionRequest);
        model.addAttribute("recommendations", recommendations);
        model.addAttribute("request", predictionRequest);
        return "recommendation";
    }
}