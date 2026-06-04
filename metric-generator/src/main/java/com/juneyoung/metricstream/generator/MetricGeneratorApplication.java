package com.juneyoung.metricstream.generator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MetricGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricGeneratorApplication.class, args);
    }
}
