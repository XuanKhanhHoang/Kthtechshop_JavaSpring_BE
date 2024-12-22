package com.kth.kthtechshop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class KthtechshopApplication {

    public static void main(String[] args) {
        SpringApplication.run(KthtechshopApplication.class, args);
    }

}
