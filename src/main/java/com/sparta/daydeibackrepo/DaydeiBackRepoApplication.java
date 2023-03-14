package com.sparta.daydeibackrepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DaydeiBackRepoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaydeiBackRepoApplication.class, args);
    }

}

