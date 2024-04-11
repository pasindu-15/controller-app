package com.msc.research.controller.service;

import com.msc.research.controller.external.aws.MetricReader;
import com.msc.research.controller.external.MetricWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class TrainService {

    @Autowired
    MetricReader metricReader;

    @Autowired
    MetricWriter metricWriter;

    public void train() {

        long duration = 900;
        Map<Instant, List<Datapoint>> sortedDpMap =  metricReader.readMetrics(duration, false);

        metricWriter.write(sortedDpMap);

    }
}
