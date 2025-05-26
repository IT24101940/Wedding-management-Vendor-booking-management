package com.dreamydayplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DreamyDayPlannerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DreamyDayPlannerApplication.class, args);
    }
}

