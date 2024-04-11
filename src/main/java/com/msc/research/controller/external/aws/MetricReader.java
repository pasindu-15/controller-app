package com.msc.research.controller.external.aws;

import com.msc.research.controller.model.MetricModel;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Service
public class MetricReader {

    @Autowired
    private CloudWatchClient cloudWatchClient;

    long duration = 0;

    Map<Instant, MetricModel> collectedCPU;
    Map<Instant, List<Datapoint>> datapointMap;

    public Map<Instant, List<Datapoint>> readMetrics(long duration, boolean isHighLevelMetricOnly) {

        collectedCPU = new HashMap<>();
        datapointMap = new HashMap<>();
        this.duration = duration;


        Dimension tgDim = Dimension.builder()
                .name("TargetGroup")
                .value("targetgroup/demo-tg-03/42c27572db3b331e")
                .build();

        Dimension azDim = Dimension.builder()
                .name("AvailabilityZone")
                .value("ap-northeast-1a")
                .build();

        Dimension elbDim = Dimension.builder()
                .name("LoadBalancer")
                .value("app/demo-LB-10/7c621bdf72c815ed")
                .build();

        Dimension cltDim = null;
        Dimension srvDim = null;

        if(!isHighLevelMetricOnly){
            cltDim = Dimension.builder()
                    .name("ClusterName")
                    .value("my-demo-cluster-2")
                    .build();

            srvDim = Dimension.builder()
                    .name("ServiceName")
                    .value("demo-service-04")
                    .build();
        }


        GetMetricStatisticsRequest reqCountPerTargetRequest = createMetricRequest(cloudWatchClient, "AWS/ApplicationELB", "RequestCount", "Sum", elbDim,tgDim);
        GetMetricStatisticsRequest tgtResponseTimeRequest = createMetricRequest(cloudWatchClient, "AWS/ApplicationELB", "TargetResponseTime", "Average", elbDim);
        GetMetricStatisticsRequest actConCountRequest = createMetricRequest(cloudWatchClient, "AWS/ApplicationELB", "ActiveConnectionCount", "Sum", elbDim);
        GetMetricStatisticsRequest newConCountRequest = createMetricRequest(cloudWatchClient, "AWS/ApplicationELB", "NewConnectionCount", "Sum", elbDim);
//        GetMetricStatisticsRequest http504CountRequest = createMetricRequest(cloudWatchClient,"AWS/ApplicationELB","HTTPCode_ELB_5XX_Count","Sum",azDim,elbDim2);
        GetMetricStatisticsRequest hstCountRequest = createMetricRequest(cloudWatchClient, "AWS/ApplicationELB", "HealthyHostCount", "Maximum", tgDim, elbDim);
        GetMetricStatisticsRequest cpuRequest = null;
        if(!isHighLevelMetricOnly){
            cpuRequest = createMetricRequest(cloudWatchClient, "AWS/ECS", "CPUUtilization", "Average", cltDim, srvDim);
        }



        // Create multiple GetMetricStatisticsRequest objects
        List<GetMetricStatisticsRequest> requests = new ArrayList<>();
        requests.add(reqCountPerTargetRequest);
        requests.add(tgtResponseTimeRequest);
        requests.add(actConCountRequest);
        requests.add(newConCountRequest);
//        requests.add(http504CountRequest);
        requests.add(hstCountRequest);

        if(!isHighLevelMetricOnly){
            requests.add(cpuRequest);
        }


        try {
            // Retrieve metrics in parallel
            List<GetMetricStatisticsResponse> results = retrieveMetrics(requests);
            // Process results

            for (GetMetricStatisticsResponse result : results) {
                // Process each result
                List<Datapoint> data = result.datapoints();

                // Create a new ArrayList from the existing list of datapoints
                List<Datapoint> sortedDatapoints = new ArrayList<>(data);

                // Sort the new list of datapoints by timestamp
                sortedDatapoints.sort(Comparator.comparing(Datapoint::timestamp));


//                if (Objects.equals(result.label(), "RequestCount")) {
                    if (!data.isEmpty()) {
                        for (Datapoint datapoint : sortedDatapoints) {
                            addValue(datapoint.timestamp(),datapoint);

//                            MetricModel mm = new MetricModel();
//                            System.out.println("Timestamp: " + datapoint.timestamp() + " Maximum TPS value: " + datapoint.sum() / 60);
                        }
                    } else {
                        System.out.println("The returned data list is empty");
                    }
//                } else {
//                    if (!data.isEmpty()) {
//                        for (Datapoint datapoint : sortedDatapoints) {
//                            addValue(datapoint.timestamp(),datapoint);
//                            System.out.println("Timestamp: " + datapoint.timestamp() + " Maximum value: " + datapoint.maximum());
//                        }
//                    } else {
//                        System.out.println("The returned data list is empty");
//                    }
//                }

            }

        } catch (InterruptedException | ExecutionException e) {
            // Handle exceptions
            e.printStackTrace();
        }
        // Convert the HashMap to a TreeMap to sort it by keys
        return new TreeMap<>(datapointMap);


    }

    private void addValue(Instant key, Datapoint value) {
        // If the key is not present in the map, create a new list for values
        datapointMap.putIfAbsent(key, new ArrayList<>());
        // Add the value to the list associated with the key
        datapointMap.get(key).add(value);
    }


//    public void getAndDisplayMetricStatistics(CloudWatchClient cw, String nameSpace, String metVal,
//                                                     String metricOption, Dimension ... myDimension) {
//        try {
//            Instant startTime = Instant.parse("2024-04-09T00:00:00Z"); // Example start time (1 hour ago)
//            Instant endTime = Instant.parse("2024-04-09T12:00:00Z"); // Example end time (now)
//
//            GetMetricStatisticsRequest statisticsRequest = GetMetricStatisticsRequest.builder()
//                    .endTime(endTime)
//                    .startTime(startTime)
//                    .dimensions(myDimension)
//                    .metricName(metVal)
//                    .namespace(nameSpace)
//                    .period(60)
//                    .statistics(Statistic.fromValue(metricOption))
//                    .build();
//
//            GetMetricStatisticsResponse response = cw.getMetricStatistics(statisticsRequest);
//            List<Datapoint> data = response.datapoints();
//            if (!data.isEmpty()) {
//                for (Datapoint datapoint : data) {
//
//                    System.out
//                            .println("Timestamp: " + datapoint.timestamp() + " Maximum value: " + datapoint.maximum());
//                }
//            } else {
//                System.out.println("The returned data list is empty");
//            }
//
//        } catch (CloudWatchException e) {
//            System.err.println(e.getMessage());
//            System.exit(1);
//        }
//    }


    public GetMetricStatisticsRequest createMetricRequest(CloudWatchClient cw, String nameSpace, String metVal,
                                                          String metricOption, Dimension... myDimension) {


//
//        Instant startTime = Instant.parse("2024-04-11T04:09:00Z"); // Example start time (1 hour ago)
//        Instant endTime = Instant.parse("2024-04-11T04:25:00Z"); // Example end time (now)

//        Instant startTime = Instant.parse("2024-04-11T07:04:00Z"); // Example start time (1 hour ago)
//        Instant endTime = Instant.parse("2024-04-11T07:20:00Z"); // Example end time (now)

//        Instant startTime = Instant.parse("2024-04-11T08:33:00Z"); // Example start time (1 hour ago)
//        Instant endTime = Instant.parse("2024-04-11T08:49:00Z"); // Example end time (now)

        Instant startTime = Instant.parse("2024-04-11T10:22:00Z"); // Example start time (1 hour ago)
        Instant endTime = Instant.parse("2024-04-11T10:59:00Z"); // Example end time (now)

//        Instant endTime = Instant.now();
//        Instant startTime = Instant.now().minusSeconds(duration); // Start time is one minute ago


        return GetMetricStatisticsRequest.builder()
                .endTime(endTime)
                .startTime(startTime)
                .dimensions(myDimension)
                .metricName(metVal)
                .namespace(nameSpace)
                .period(60)
                .statistics(Statistic.fromValue(metricOption))
                .build();


    }


    public List<GetMetricStatisticsResponse> retrieveMetrics(List<GetMetricStatisticsRequest> requests) throws InterruptedException, ExecutionException {
        List<CompletableFuture<GetMetricStatisticsResponse>> futures = new ArrayList<>();

        // Create CompletableFuture objects for each GetMetricStatistics request
        for (GetMetricStatisticsRequest request : requests) {
            CompletableFuture<GetMetricStatisticsResponse> future = CompletableFuture.supplyAsync(
                    () -> cloudWatchClient.getMetricStatistics(request)
            );
            futures.add(future);
        }

        // Execute all CompletableFuture objects in parallel
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

        // Wait for all CompletableFuture objects to complete
        allFutures.get();

        // Collect the results from all CompletableFuture objects
        List<GetMetricStatisticsResponse> results = new ArrayList<>();
        for (CompletableFuture<GetMetricStatisticsResponse> future : futures) {
            results.add(future.get());
        }

        return results;
    }
}


//
//    public List<MergedType> mergeLists(List<Datapoint> listA, List<Datapoint> listB, List<Datapoint> listC) {
//        List<MergedType> mergedList = new ArrayList<>();
//
//        // Convert TypeA objects to MergedType and add to mergedList
//        for (Datapoint obj : listA) {
//            mergedList.add(new MergedType(obj.timestamp()));
//        }
//
//        // Convert TypeB objects to MergedType and add to mergedList
//        for (Datapoint obj : listB) {
//            mergedList.add(new MergedType(obj.timestamp()));
//        }
//
//        // Convert TypeC objects to MergedType and add to mergedList
//        for (Datapoint obj : listC) {
//            mergedList.add(new MergedType(obj.timestamp()));
//        }
//
//        return mergedList;
//    }
//}
//
//class MergedType {
//    private Instant commonField;
//    // Add other fields as needed
//
//    public MergedType(Instant commonField) {
//        this.commonField = commonField;
//    }
//
//    public Instant getCommonField() {
//        return commonField;
//    }
//
//    // Add setters and getters for other fields
//}


