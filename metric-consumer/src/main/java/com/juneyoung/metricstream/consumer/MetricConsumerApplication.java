package com.juneyoung.metricstream.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MetricConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricConsumerApplication.class, args);
    }
}
