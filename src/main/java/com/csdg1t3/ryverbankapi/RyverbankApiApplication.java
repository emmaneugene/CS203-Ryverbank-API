package com.csdg1t3.ryverbankapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class RyverbankApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(RyverbankApiApplication.class, args);
    }
}
