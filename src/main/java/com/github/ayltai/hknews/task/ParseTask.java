package com.github.ayltai.hknews.task;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.diagnostic.AgentFactory;
import com.github.ayltai.hknews.net.ApiServiceFactory;
import com.github.ayltai.hknews.parser.Parser;
import com.github.ayltai.hknews.parser.ParserFactory;
import com.instrumentalapp.Agent;

@Component
public class ParseTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParseTask.class);

    private static final String METRIC_TASK       = "app.task.parse";
    private static final String METRIC_TASK_ITEMS = "app.task.parse.items";

    private final ApiServiceFactory apiServiceFactory;
    private final SourceRepository  sourceRepository;
    private final ItemRepository    itemRepository;
    private final AgentFactory      agentFactory;

    private Agent agent;

    @Autowired
    public ParseTask(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull@lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository, @NonNull @lombok.NonNull final AgentFactory agentFactory) {
        this.apiServiceFactory = apiServiceFactory;
        this.sourceRepository  = sourceRepository;
        this.itemRepository    = itemRepository;
        this.agentFactory      = agentFactory;
    }

    @CacheEvict(
        cacheNames = "items",
        allEntries = true
    )
    @Scheduled(
        initialDelay = 60 * 1000,
        fixedRate    = 15 * 60 * 1000)
    public void parse() {
        final long startTime = System.currentTimeMillis();

        final ParserFactory factory = ParserFactory.getInstance(this.apiServiceFactory, this.sourceRepository, this.itemRepository);

        this.sourceRepository
            .findAll()
            .forEach(source -> source.getCategories()
                .forEach(category -> this.parse(factory, source, category)));

        if (this.agent == null) this.agent = this.agentFactory.create();
        if (this.agent != null) this.agent.gauge(ParseTask.METRIC_TASK, System.currentTimeMillis() - startTime);
    }

    @Async
    protected void parse(@NonNull @lombok.NonNull final ParserFactory factory, @NonNull @lombok.NonNull final Source source, @NonNull @lombok.NonNull final Category category) {
        final long startTime = System.currentTimeMillis();

        try (Parser parser = factory.create(source.getName())) {
            parser.getItems(category).forEach(item -> this.parse(factory, source, item));

            if (this.agent == null) this.agent = this.agentFactory.create();
            if (this.agent != null) this.agent.gauge(ParseTask.METRIC_TASK_ITEMS, System.currentTimeMillis() - startTime);
        } catch (final Exception e) {
            ParseTask.LOGGER.error("An unexpected error has occurred for category: " + category.getName(), e);
        }
    }

    @Async
    protected void parse(@NonNull @lombok.NonNull final ParserFactory factory, @NonNull @lombok.NonNull final Source source, @NonNull @lombok.NonNull final Item item) {
        try (Parser parser = factory.create(source.getName())) {
            if (this.itemRepository.findByUrl(item.getUrl()) == null) this.itemRepository.save(parser.getItem(item));
        } catch (final IOException e) {
            ParseTask.LOGGER.error("Failed to parse URL: " + item.getUrl(), e);
        } catch (final Exception e) {
            ParseTask.LOGGER.error("An unexpected error has occurred for URL: " + item.getUrl(), e);
        }
    }
}
