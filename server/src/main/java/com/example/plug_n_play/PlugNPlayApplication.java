package com.example.plug_n_play;

import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync(proxyTargetClass = true)
public class PlugNPlayApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlugNPlayApplication.class, args);
    }

    @Bean
    public TokenTextSplitter tokenTextSplitter(){
        return new TokenTextSplitter();
    }
}
