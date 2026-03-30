package com.queenstouch.queenstouchbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class QueenstouchBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(QueenstouchBackendApplication.class, args);
    }

}
