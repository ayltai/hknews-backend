package com.github.ayltai.hknews.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.SourceRepository;

@RestController
@RequestMapping("/sources")
public class SourceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SourceController.class);

    private final SourceRepository sourceRepository;

    public SourceController(@NonNull @lombok.NonNull final SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @NonNull
    @Cacheable(
        cacheNames = "sources",
        sync       = true
    )
    @GetMapping(produces = "application/json")
    public Iterable<Source> getSources() {
        if (this.sourceRepository.count() == 0) {
            final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sources.json");
            if (inputStream != null) {
                try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    this.sourceRepository.saveAll(new Gson().fromJson(reader, new TypeToken<List<Source>>() {}.getType()));
                } catch (final IOException e) {
                    SourceController.LOGGER.error(e.getMessage(), e);
                }
            }
        }

        return this.sourceRepository.findAll();
    }
}
