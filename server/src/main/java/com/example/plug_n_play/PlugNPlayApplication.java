package com.example.plug_n_play;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class PlugNPlayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlugNPlayApplication.class, args);
    }

}
