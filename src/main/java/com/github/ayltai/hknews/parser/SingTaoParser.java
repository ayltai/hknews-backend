package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

public final class SingTaoParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingTaoParser.class);

    //region Constants

    private static final String BASE_URI = "http://std.stheadline.com/daily/";
    private static final String CLOSE    = "</div>";
    private static final String QUOTE    = "\"";

    private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    //endregion

    @Getter
    private final Source source;

    SingTaoParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_SING_TAO);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull @lombok.NonNull final Category category) {
        if (category.getUrls().isEmpty()) return Collections.emptyList();

        return category.getUrls()
            .stream()
            .map(url -> {
                try {
                    return StringUtils.substringsBetween(StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(url).execute().body(), "<div class=\"main list\">", "input type=\"hidden\" id=\"totalnews\""), "underline\">", "</a>\n</div>");
                } catch (final IOException e) {
                    SingTaoParser.LOGGER.error(e.getMessage(), e);

                    return null;
                }
            })
            .filter(Objects::nonNull)
            .map(Arrays::asList)
            .collect((Supplier<List<String>>)ArrayList::new, List::addAll, List::addAll)
            .stream()
            .map(section -> {
                final String url = StringUtils.substringBetween(section,  "<a href=\"", SingTaoParser.QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "<i class=\"fa fa-clock-o\"></i>", SingTaoParser.CLOSE);
                if (date == null) return null;

                try {
                    final Item item = new Item();

                    item.setTitle(StringUtils.substringBetween(section, "<div class=\"title\">", SingTaoParser.CLOSE).replace("<h1>", "").replace("</h1>", ""));
                    item.setDescription(StringUtils.substringBetween(section, "<div class=\"des\">　　(星島日報報道)", SingTaoParser.CLOSE));
                    item.setUrl(SingTaoParser.BASE_URI + url);
                    item.setPublishDate(Parser.toSafeDate(SingTaoParser.DATE_FORMAT.get().parse(date)));
                    item.setSource(this.getSource());
                    item.setCategory(category);

                    return item;
                } catch (final ParseException e) {
                    SingTaoParser.LOGGER.warn(e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection((Supplier<Collection<Item>>)ArrayList::new));
    }

    @NonNull
    @Override
    public Item getItem(@NonNull @lombok.NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new IllegalArgumentException("Item URL cannot be null");

        final String html = StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div class=\"post-content\">", "<div class=\"post-sharing\">");

        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html, "<p>", "</p>");
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce("", (description, content) -> description + content.trim() + "<br>"));

            final String[] imageContainers = StringUtils.substringsBetween(html, "<a class=\"fancybox-thumb\"", ">");
            if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "href=\"", SingTaoParser.QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "title=\"", SingTaoParser.QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection((Supplier<Collection<Image>>)ArrayList::new)));
        }

        return item;
    }
}
