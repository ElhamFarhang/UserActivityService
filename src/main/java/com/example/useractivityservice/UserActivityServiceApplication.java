package com.example.useractivityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.useractivity.clients")
public class UserActivityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserActivityServiceApplication.class, args);
    }

}
