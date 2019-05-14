package com.github.ayltai.hknews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.lang.Nullable;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties({ AppConfig.class })
public class MainApplication {
    public static void main(@Nullable final String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }
}
