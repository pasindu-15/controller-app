package com.msc.research.controller.service;

import com.msc.research.controller.external.MLPrediction;
import com.msc.research.controller.external.aws.ECSService;
import com.msc.research.controller.external.aws.MetricReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class ScalingService {
    @Autowired
    MetricReader metricReader;

    @Autowired
    ECSService ecsService;

    @Autowired
    MLPrediction prediction;

    String clusterName = "my-demo-cluster-2";
    String serviceName = "demo-service-04";



    public void readAndScale() {

//        long duration = 150;
//        Map<Instant, List<Datapoint>> sortedDpMap = metricReader.readMetrics(duration, true);
//
//        Map.Entry<Instant,List<Datapoint>> recentData = null;
//        for (Map.Entry<Instant,List<Datapoint>> entry : sortedDpMap.entrySet()) {
//            recentData = entry;
//        }
        //TODO: predict the CPU
        prediction.predictCPU();
        double cpuPred = prediction.predictCPU();;

        double desiredInsCount = Math.ceil(cpuPred/0.2);  // 0.5 cpu x 40% threshold

//        double existingInsCount = recentData.getValue().get(4).maximum();

//        if(desiredInsCount != existingInsCount){
//            //TODO: execute the scaling action with insCount
////            ecsService.adjustTaskCount(clusterName,serviceName, (int)desiredInsCount);
//            ecsService.adjustTaskCount(clusterName,serviceName, 2);
//
//        }
        System.out.println();


    }
}
