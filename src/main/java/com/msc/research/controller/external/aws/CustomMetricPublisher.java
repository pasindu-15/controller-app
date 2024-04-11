package com.msc.research.controller.external.aws;

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;

import java.time.Instant;
import java.util.Collections;

public class CustomMetricPublisher {

    private CloudWatchClient cloudWatchClient;

    public CustomMetricPublisher(CloudWatchClient cloudWatchClient) {
        this.cloudWatchClient = cloudWatchClient;
    }

    public void publishCpuUtilization(double cpuUtilization) {


        Dimension dimension = Dimension.builder().name("ServiceName").value("demo-service-04").build();

        MetricDatum metricDatum = MetricDatum.builder()
                .metricName("CPUUtilization") // Replace with your metric name
                .dimensions(Collections.singletonList(dimension))
                .unit(StandardUnit.PERCENT) // Replace with appropriate unit
                .value(cpuUtilization)
                .timestamp(Instant.now())
                .build();

        // Create PutMetricDataRequest
        PutMetricDataRequest request = PutMetricDataRequest.builder()
                .namespace("Custom/ECS") // Replace with your namespace
                .metricData(metricDatum)
                .build();



        cloudWatchClient.putMetricData(request);
    }
}

