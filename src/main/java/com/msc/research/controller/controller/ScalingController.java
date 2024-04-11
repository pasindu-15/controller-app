package com.msc.research.controller.controller;

import com.msc.research.controller.service.ScalingService;
import com.msc.research.controller.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@RestController

@RequestMapping("/scaling-service")
public class ScalingController {


    @Autowired
    ScalingService scalingService;

    @GetMapping(value = "/scale", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map>> findAllBook(HttpServletRequest request) throws IOException {

        scalingService.readAndScale();

        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
