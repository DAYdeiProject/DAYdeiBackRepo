package com.sparta.daydeibackrepo;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
//@OpenAPIDefinition(servers = {@Server(url = "https://daydei.shop", description = "Default Server URL") ,@Server(url = "http://localhost:8080", description = "Local Server URL")})
@EnableAsync
@SpringBootApplication
public class DaydeiBackRepoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DaydeiBackRepoApplication.class, args);
    }

}

