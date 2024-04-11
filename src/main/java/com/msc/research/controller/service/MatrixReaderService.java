//package com.msc.research.controller.service;
//
//import org.springframework.stereotype.Service;
//import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
//import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
//import com.amazonaws.services.cloudwatch.model.*;
//
//@Service
//public class MatrixReaderService {
//
//    AmazonCloudWatch cloudWatchClient = AmazonCloudWatchClient.builder().build();
//
//    GetMetricStatisticsRequest request = new GetMetricStatisticsRequest()
//            .withNamespace("AWS/EC2")
//            .withMetricName("CPUUtilization")
//            .withStartTime(new java.util.Date(System.currentTimeMillis() - 60 * 1000))  // Start time
//            .withEndTime(new java.util.Date())
//            .withPeriod(1)  // 1-second granularity
//            .withStatistics("Average")
//            .withDimensions(new Dimension().withName("InstanceId").withValue("your-instance-id"));
//
//    GetMetricStatisticsResult result = cloudWatchClient.getMetricStatistics(request);
//    // Handle and parse the metric data in the result, e.g., result.getDatapoints()
//
//}
