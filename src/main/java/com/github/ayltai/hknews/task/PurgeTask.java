package com.github.ayltai.hknews.task;

import com.github.ayltai.hknews.AppConfig;
import com.github.ayltai.hknews.data.repository.ItemRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class PurgeTask {
    private final ItemRepository itemRepository;
    private final AppConfig config;

    @Autowired
    public PurgeTask(@NonNull @lombok.NonNull final ItemRepository itemRepository, @NonNull @lombok.NonNull final AppConfig config) {
        this.itemRepository = itemRepository;
        this.config         = config;
    }

    @Async
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    public void purge() {
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -this.config.getRetentionDays());

        this.itemRepository.deleteByPublishDateBefore(calendar.getTime());
    }
}
