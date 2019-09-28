package com.github.ayltai.hknews.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.service.SourceService;

@Component
public class InitTask {
    private final SourceRepository sourceRepository;

    @Autowired
    public InitTask(@NonNull @lombok.NonNull final SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    @Scheduled(
        initialDelay = 30 * 1000,
        fixedDelay   = Long.MAX_VALUE
    )
    public void init() {
        new SourceService(this.sourceRepository).getAllSources(PageRequest.of(0, 100));
    }
}
