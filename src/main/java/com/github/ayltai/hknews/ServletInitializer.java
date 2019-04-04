package com.github.ayltai.hknews;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.lang.NonNull;

public final class ServletInitializer extends SpringBootServletInitializer {
    @NonNull
    @Override
    protected SpringApplicationBuilder configure(@NonNull @lombok.NonNull final SpringApplicationBuilder application) {
        return application.sources(MainApplication.class);
    }
}
