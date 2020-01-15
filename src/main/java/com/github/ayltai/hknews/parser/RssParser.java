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
import java.util.stream.Collectors;

import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;
import com.github.ayltai.hknews.rss.Enclosure;
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

        final List<Feed> feeds = new ArrayList<>();
        for (final String url : category.getUrls()) {
            try {
                final Feed feed = this.apiServiceFactory
                    .create()
                    .getFeed(url)
                    .execute()
                    .body();

                if (feed == null || feed.getItems() == null || feed.getItems().isEmpty()) continue;

                feeds.add(feed);
            } catch (final IOException e) {
                LoggerFactory.getLogger(this.getClass()).error("Error downloading contents from URL: " + url, e);
            }
        }

        if (feeds.isEmpty()) return Collections.emptyList();

        return feeds.stream()
            .map(Feed::getItems)
            .flatMap(Collection::stream)
            .collect(Collectors.toList())
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

                    this.extractImages(item, rssItem);
                    this.extractVideos(item, rssItem);

                    return item;
                } catch (final ParseException e) {
                    LoggerFactory.getLogger(this.getClass()).warn(e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void close() {
        RssParser.DATE_FORMAT.remove();
    }

    private void extractImages(@NonNull @lombok.NonNull final Item item, @NonNull @lombok.NonNull final com.github.ayltai.hknews.rss.Item rssItem) {
        if (rssItem.getEnclosures() == null || rssItem.getEnclosures().isEmpty()) return;

        rssItem.getEnclosures()
            .stream()
            .filter(Enclosure::isImage)
            .forEach(enclosure -> {
                final Image image = new Image();
                image.setImageUrl(enclosure.getUrl());

                item.getImages().add(image);
            });
    }

    private void extractVideos(@NonNull @lombok.NonNull final Item item, @NonNull @lombok.NonNull final com.github.ayltai.hknews.rss.Item rssItem) {
        if (rssItem.getEnclosures() == null || rssItem.getEnclosures().isEmpty()) return;

        rssItem.getEnclosures()
            .stream()
            .filter(Enclosure::isVideo)
            .forEach(enclosure -> {
                if (item.getImages().isEmpty()) return;

                final Video video = new Video();
                video.setImageUrl(item.getImages().get(0).getImageUrl());
                video.setVideoUrl(enclosure.getUrl());

                item.getVideos().add(video);
            });
    }
}
