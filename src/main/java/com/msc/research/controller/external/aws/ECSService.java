package com.msc.research.controller.external.aws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ecs.EcsClient;
import software.amazon.awssdk.services.ecs.model.UpdateServiceRequest;

@Service
public class ECSService {

    private final EcsClient ecsClient;

    @Autowired
    public ECSService(EcsClient ecsClient) {
        this.ecsClient = ecsClient;
    }

    public void adjustTaskCount(String clusterName, String serviceName, int desiredCount) {
        UpdateServiceRequest request = UpdateServiceRequest.builder()
                .cluster(clusterName)
                .service(serviceName)
                .desiredCount(desiredCount)
                .build();

        ecsClient.updateService(request);

    }
}