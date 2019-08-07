package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.core.parameters.P;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;
import com.github.ayltai.hknews.rss.Feed;

public abstract class RssParser extends Parser {
    private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH));

    RssParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);
    }

    @NonNull
    @Override
    public final Collection<Item> getItems(@NonNull @lombok.NonNull final Category category) {
        if (category.getUrls().isEmpty()) return Collections.emptyList();

        return category.getUrls()
            .stream()
            .map(url -> {
                try {
                    return this.apiServiceFactory
                        .create()
                        .getFeed(url)
                        .execute()
                        .body();
                } catch (final IOException e) {
                    LoggerFactory.getLogger(this.getClass()).error(e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .map(Feed::getItems)
            .collect((Supplier<List<com.github.ayltai.hknews.rss.Item>>)ArrayList::new, List::addAll, List::addAll)
            .stream()
            .filter(rssItem -> Objects.nonNull(rssItem.getLink()))
            .map(rssItem -> {
                try {
                    final Item item = new Item();

                    item.setTitle(rssItem.getTitle());
                    item.setDescription(rssItem.getDescription().trim());
                    item.setUrl(rssItem.getLink().trim());
                    item.setPublishDate(Parser.toSafeDate(RssParser.DATE_FORMAT.get().parse(rssItem.getPubDate().trim())));
                    item.setSource(this.getSource());
                    item.setCategory(category);

                    return item;
                } catch (final ParseException e) {
                    LoggerFactory.getLogger(this.getClass()).warn(e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection((Supplier<Collection<Item>>)ArrayList::new));
    }
}
