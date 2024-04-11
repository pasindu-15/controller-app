package com.msc.research.controller.external;

import com.msc.research.controller.model.PredictionModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MLPrediction {

    private final RestTemplate restTemplate;

    public MLPrediction(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double predictCPU(){

        String apiUrl = "http://localhost:5000/cpu-prediction";

        PredictionModel predictionModel = restTemplate.getForObject(apiUrl, PredictionModel.class);
        // You can process the JSON response further here

        return predictionModel.getPrediction();
    }
}
