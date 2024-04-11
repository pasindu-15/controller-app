package com.msc.research.controller.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClientBuilder;
import software.amazon.awssdk.services.ecs.EcsClient;


@Configuration
public class AWSConfig {

//    @Bean
//    public AmazonCloudWatch cloudWatchClient1() {
//               return AmazonCloudWatchClient.builder()
//                       .withRegion(Regions.AP_NORTHEAST_1)
//                       .withCredentials(new DefaultAWSCredentialsProviderChain())
//                       .build();
//    }
//
//    @Bean
//    public AmazonECS ecsClient() {
//        return AmazonECSClient.builder()
//                .withRegion(Regions.AP_NORTHEAST_1) // Set region if needed
//                .build();
//
//    }
//
//    @Bean
//    public CloudWatchClient cloudWatchClient() {
//        // Choose your preferred credential provider approach here
//        return new CloudWatchClientBuilder().credentialsProvider()
//                (credentialsProvider()); // Replace with your provider method
//    }
//
//    private  credentialsProvider() {
//        return new AWSStaticCredentialsProvider(
//                new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"),
//                        System.getenv("AWS_SECRET_ACCESS_KEY"))
//        );
//    }

    @Bean
    public CloudWatchClient cloudWatchClient() {
        return CloudWatchClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.AP_NORTHEAST_1) // Replace with your AWS region
                .build();
    }

    @Bean
    public EcsClient ecsClient() {
        return EcsClient.builder()
                .credentialsProvider(DefaultCredentialsProvider.create())
                .region(Region.AP_NORTHEAST_1) // Replace with your AWS region
                .build();
    }

}