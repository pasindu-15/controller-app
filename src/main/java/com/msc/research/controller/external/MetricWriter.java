package com.msc.research.controller.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudwatch.model.Datapoint;
import software.amazon.awssdk.services.cloudwatch.model.MetricDataResult;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Service
public class MetricWriter {

    @Value("${matrix.export.file-path}")
    private String csvFilePath;

    public void write(Map<Instant, List<Datapoint>> sortedMap){
//         Write data to CSV file


        try (FileWriter writer = new FileWriter(csvFilePath)) {
//            writer.append("Timestamp,RequestCount,TargetResponseTime,ActiveConnectionCount,NewConnectionCount,HealthyHostCount,CPUUtilization,CPUUnits\n");
            writer.append("Timestamp,RequestCount,TargetResponseTime,ActiveConnectionCount,NewConnectionCount,CPUUnits\n");

            for (Map.Entry<Instant, List<Datapoint>> m : sortedMap.entrySet()) {
                List<Object> values = new ArrayList<>();

//                values.add(m.getKey().toString());

                for (Datapoint dp : m.getValue()) {
                    if (Objects.nonNull(dp.average())) {

                        values.add(dp.average());
                    } else if (Objects.nonNull(dp.sum())) {

                        values.add(dp.sum());
                    } else if (Objects.nonNull(dp.maximum())) {

                        values.add(dp.maximum());
                    }

                }
                double cpuUnits = ((double)values.get(5) * (double)values.get(4))/200;
                values.remove(4);
                values.remove(4);
                values.add(cpuUnits);

                String line = String.join(", ", values.toString());
                writer.append(line.substring(1, line.length() - 1) + "\n");

            }

            System.out.println("CSV file generated successfully: " + csvFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
