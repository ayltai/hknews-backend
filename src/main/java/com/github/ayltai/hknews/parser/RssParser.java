package com.github.ayltai.hknews.parser;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;
import com.github.ayltai.hknews.rss.Feed;

import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class RssParser extends Parser {
    private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH));

    RssParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);
    }

    @NonNull
    @Override
    public final Collection<Item> getItems(@NonNull final Category category) throws IOException {
        if (category.getUrl() == null) return Collections.emptyList();

        final Feed feed = this.apiServiceFactory
            .create()
            .getFeed(category.getUrl())
            .execute()
            .body();

        if (feed.getItems() == null) return Collections.emptyList();

        return feed.getItems()
            .parallelStream()
            .filter(rssItem -> rssItem.getLink() != null)
            .map(rssItem -> {
                try {
                    final Item item = new Item();

                    item.setDescription(rssItem.getDescription().trim());
                    item.setUrl(rssItem.getTitle().trim());
                    item.setPublishDate(RssParser.DATE_FORMAT.get().parse(rssItem.getPubDate().trim()));
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
