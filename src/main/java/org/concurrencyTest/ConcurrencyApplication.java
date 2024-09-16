package org.concurrencyTest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class ConcurrencyApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConcurrencyApplication.class, args);
    }
}

