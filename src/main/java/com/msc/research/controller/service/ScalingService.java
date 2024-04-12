package com.msc.research.controller.service;

import com.msc.research.controller.external.MLPrediction;
import com.msc.research.controller.external.aws.ECSService;
import com.msc.research.controller.external.aws.MetricReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;

import java.time.Instant;
import java.util.*;

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

        long duration = 200;
        double desiredInsCount;
        TreeMap<Instant, List<Datapoint>> sortedDpMap = metricReader.readMetrics(duration, true);


        List<Datapoint> recentData =  null;
//        for (TreeMap<Instant,List<Datapoint>> entry : sortedDpMap.) {
//            recentData = entry;
//        }

        for( int i = sortedDpMap.size()-1 ; i >=0; i-- ){

            recentData = sortedDpMap.lastEntry().getValue();

            if(sortedDpMap.lastEntry().getValue().size() != 5){
                sortedDpMap.remove(sortedDpMap.lastKey());
                continue;
            }else{

                break;
            }
        }
        if(recentData.size() != 5){
            desiredInsCount = 1;
        }else{
            double cpuPred = prediction.predictCPU(recentData);
            System.out.println(recentData.get(0).timestamp() +": predicted CPU :"+ cpuPred);

            desiredInsCount = Math.ceil(cpuPred/0.2);  // 0.5 cpu x 40% threshold

        }

        double existingInsCount = 0;
        for(Datapoint dp : recentData){
            if(Objects.nonNull(dp.maximum())){
                existingInsCount = dp.maximum();
            }
        }

        if(desiredInsCount != existingInsCount){
//            //execute the scaling action with insCount

            ecsService.adjustTaskCount(clusterName,serviceName, (int)desiredInsCount);
            System.out.println("desired instant count :"+desiredInsCount);


        }

    }
}
