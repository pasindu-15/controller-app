package com.msc.research.controller.external;

import com.msc.research.controller.model.PredictionModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;

import java.time.Instant;
import java.util.*;

@Service
public class MLPrediction {

    private final RestTemplate restTemplate;

    public MLPrediction(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public double predictCPU(List<Datapoint> dataPoints){

        PredictionModel predictionModel = null;
        try {
            String apiUrl = "http://localhost:5000/cpu-prediction?";
            if(dataPoints.size() == 2){
                apiUrl += "req="+dataPoints.get(0).sum()+"&res=0.0&act=0.0&new=0.0";

            }else{
                apiUrl += "req="+dataPoints.get(0).sum()+"&res="+dataPoints.get(1).average()+"&act="+dataPoints.get(2).sum()+"&new="+dataPoints.get(3).sum();

            }
            predictionModel = restTemplate.getForObject(apiUrl, PredictionModel.class);

        }catch (Exception e){

            e.printStackTrace();
        }

        return predictionModel.getPrediction();
    }
}
