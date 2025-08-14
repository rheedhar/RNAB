package com.rnab.rnab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RnabApplication {

    public static void main(String[] args) {
        SpringApplication.run(RnabApplication.class, args);
    }

}
