package com.github.ayltai.hknews.parser;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;

import io.micrometer.core.annotation.Timed;

public final class RthkParser extends RssParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(RthkParser.class);

    private final Source source;

    RthkParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_RTHK);
    }

    @NonNull
    @Override
    public Source getSource() {
        return this.source;
    }

    @NonNull
    @Timed(
        value     = "parser_get_item",
        extraTags = { "rthk" }
    )
    @Override
    public Item getItem(@NonNull @lombok.NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new IllegalArgumentException("Item URL cannot be null");

        final String html = this.apiServiceFactory
            .create()
            .getHtml(item.getUrl())
            .execute()
            .body();

        if (html != null) {
            item.setDescription(StringUtils.substringBetween(html, "<div class=\"itemFullText\">", "</div>"));

            if (item.getDescription() == null) {
                RthkParser.LOGGER.warn("Failed to parse description for " + this.getSource().getName() + ": " + item.getUrl());

                return item;
            }

            item.setDescription(item.getDescription().trim());

            final String imageContainer = StringUtils.substringBetween(html, "<div class=\"itemSlideShow\">", "<div class=\"clr\"></div>");
            if (imageContainer != null) {
                final String imageUrl = StringUtils.substringBetween(imageContainer, "<a href=\"", "\"");
                if (imageUrl != null) {
                    item.getImages().add(new Image(imageUrl, StringUtils.substringBetween(imageContainer, "alt=\"", "\"")));
                }

                final String videoUrl     = StringUtils.substringBetween(imageContainer, "var videoFile\t\t\t= '", "'");
                final String thumbnailUrl = StringUtils.substringBetween(imageContainer, "var videoThumbnail\t\t= '", "'");

                if (videoUrl != null && thumbnailUrl != null) item.getVideos().add(new Video(videoUrl, thumbnailUrl));
            }
        }

        return item;

    }
}
