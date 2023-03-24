package com.sparta.daydeibackrepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@EnableAsync
@SpringBootApplication
public class DaydeiBackRepoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaydeiBackRepoApplication.class, args);
    }

}

