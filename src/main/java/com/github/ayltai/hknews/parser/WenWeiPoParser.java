package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;

import lombok.Getter;

public final class WenWeiPoParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(WenWeiPoParser.class);

    //region Constants

    private static final String CLOSE_QUOTE     = "\"";
    private static final String CLOSE_PARAGRAPH = "</p>";
    private static final String LINE_BREAKS     = "<br><br>";

    //endregion

    @Getter
    private final Source source;

    WenWeiPoParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_WEN_WEI_PO);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull @lombok.NonNull final Category category) {
        if (category.getUrls().isEmpty()) return Collections.emptyList();

        return category.getUrls()
            .stream()
            .map(url -> {
                try {
                    return StringUtils.substringsBetween(this.apiServiceFactory.create().getHtml(url).execute().body(), "<div class=\"content-art-box\">", "</article>");
                } catch (final IOException e) {
                    WenWeiPoParser.LOGGER.error(e.getMessage(), e);

                    return null;
                }
            })
            .filter(Objects::nonNull)
            .map(Arrays::asList)
            .flatMap(Collection::stream)
            .collect(Collectors.toList())
            .stream()
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<a href=\"", WenWeiPoParser.CLOSE_QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "<p class=\"date\">[ ", " ]</p>");
                if (date == null) return null;

                final Item item = new Item();

                item.setTitle(StringUtils.substringBetween(section, "target=\"_blank\">", "</a>"));
                item.setDescription(StringUtils.substringBetween(section, "<p class=\"txt\">", WenWeiPoParser.CLOSE_PARAGRAPH));
                item.setUrl(url);
                item.setSource(this.getSource());
                item.setCategory(category);

                final String[] tokens   = date.split("日 ");
                final String[] times    = tokens[1].split(":");
                final Calendar calendar = Calendar.getInstance();

                calendar.set(Calendar.DATE, Integer.parseInt(tokens[0]));
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
                calendar.set(Calendar.MINUTE, Integer.parseInt(times[1]));

                item.setPublishDate(Parser.toSafeDate(calendar.getTime()));

                return item;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection((Supplier<Collection<Item>>)ArrayList::new));
    }

    @NonNull
    @Override
    public Item getItem(@NonNull @lombok.NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new IllegalArgumentException("Item URL cannot be null");

        final String html = StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<!-- Content start -->", "!-- Content end -->");

        if (html != null) {
            String[] descriptions = StringUtils.substringsBetween(html, "<p >", WenWeiPoParser.CLOSE_PARAGRAPH);
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce("", (description, content) -> description + content + WenWeiPoParser.LINE_BREAKS));

            descriptions = StringUtils.substringsBetween(html, "<p>", WenWeiPoParser.CLOSE_PARAGRAPH);
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce(item.getDescription() == null ? "" : item.getDescription(), (description, content) -> description + content + WenWeiPoParser.LINE_BREAKS));

            final String[] imageContainers = StringUtils.substringsBetween(html, "<img ", ">");
            if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "src=\"", WenWeiPoParser.CLOSE_QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "alt=\"", WenWeiPoParser.CLOSE_QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection((Supplier<Collection<Image>>)ArrayList::new)));
        }

        return item;
    }
}
