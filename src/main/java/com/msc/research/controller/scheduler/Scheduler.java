package com.msc.research.controller.scheduler;


import com.msc.research.controller.service.ScalingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;


public class Scheduler {

    @Autowired
    ScalingService scalingService;

    @Scheduled(fixedRate = 60000)
    public void readAndSchedule(){

        scalingService.readAndScale();



    }

}
