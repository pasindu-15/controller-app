//package com.msc.research.controller.service;
//
//
//
//import com.amazonaws.services.appfabric.model.ResultStatus;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
//import software.amazon.awssdk.services.cloudwatch.model.*;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.time.Instant;
//import java.util.List;
//
//@Service
//public class AWSECSMetrixReaderService {
//
//
//
//    @Value("${aws.ec2.auto-scaling-group.name}")
//    private String autoScalingGroupName;
//
//    @Value("${matrix.export.file-path}")
//    private String csvFilePath;
//
//
//    private String serviceName = "my-demo-cluster-2/demo-service-04";
//
//    @Autowired
//    CloudWatchClient cloudWatchClient;
//
//
//
//    public void readMetrix() throws IOException {
//        // Define the CloudWatch metric queries for CPU and memory
//
//        Metric cpuMetric = Metric.builder()
//                .namespace("AWS/ECS")
//                .metricName("CPUUtilisation")
//                .dimensions(Dimension.builder()
//                        .name("ServiceName")
//                        .value(serviceName)
//                        .build())
//                .build();
//
////        MetricDataQuery cpuQuery = MetricDataQuery.builder()
////                .metricStat(MetricStat.builder()
////                        .metric(Metric.builder().namespace("AWS/ECS").metricName("CPUUtilization").dimensions(Dimension.builder().name("ServiceName").value(serviceName).build()).build())
////                        .period(1)
////                        .stat("Average")
////                        .build())
////                .id("cpu")
////                .build();
//
//        MetricDataQuery cpuQuery = MetricDataQuery.builder()
//                .metricStat(MetricStat.builder()
//                        .metric(cpuMetric)
//                        .period(1) // Period in seconds (5 minutes)
//                        .stat("Average") // Statistic (Average)
//                        .build())
//                .id("cpu")
//                .returnData(true)
//                .build();
//
//
////
////        MetricDataQuery cpuQuery = MetricDataQuery.builder()
////                .metricStat(MetricStat.builder()
////                        .metric(Metric.builder().namespace("AWS/ECS").metricName("CPUUtilization").dimensions(Dimension.builder().name("ServiceName").value(serviceName).build()).build())
////                        .period(1)
////                        .stat("Average")
////                        .build())
////                .id("cpu-utilization")
////                .returnData(true)
////                .build();
//
//
//        GetMetricDataRequest request = GetMetricDataRequest.builder()
//                .startTime(Instant.now().minusSeconds(3600)) // 1 hour ago = 3600
//                .endTime(Instant.now())
//                .metricDataQueries(cpuQuery)
//                .build();
//
//        GetMetricDataResponse result = cloudWatchClient.getMetricData(request);
//        // Write data to CSV file
//        try (FileWriter writer = new FileWriter(csvFilePath)) {
//            writer.append("Timestamp,CPUUtilization\n");
//
//            for (MetricDataResult metricDataResult : result.metricDataResults()) {
//                if (metricDataResult.statusCode().equals(ResultStatus.FAILED)) {
//                    System.err.println("Failed to retrieve data for metric: " + metricDataResult.id());
//                } else {
//                    List<Double> values = metricDataResult.values();
//                    List<Instant> timestamps = metricDataResult.timestamps();
//
//                    for (int i = 0; i < values.size(); i++) {
//                        writer.append(timestamps.get(i).toString());
//                        writer.append(",");
//                        writer.append(values.get(i).toString());
//                        writer.append("\n");
//                    }
//                }
//            }
//            System.out.println("CSV file generated successfully: " + csvFilePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//}
