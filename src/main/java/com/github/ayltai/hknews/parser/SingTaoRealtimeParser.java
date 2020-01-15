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
import java.util.regex.Pattern;
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

public final class SingTaoRealtimeParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingTaoRealtimeParser.class);

    //region Constants

    private static final String CLOSE = "</div>";
    private static final String QUOTE = "\"";

    private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm"));

    //endregion

    @Getter
    private final Source source;

    SingTaoRealtimeParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_SING_TAO_REALTIME);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull @lombok.NonNull final Category category) {
        if (category.getUrls().isEmpty()) return Collections.emptyList();

        final List<String[]> htmls = new ArrayList<>();
        for (final String url : category.getUrls()) {
            try {
                final String[] html = StringUtils.substringsBetween(this.apiServiceFactory.create().getHtml(url).execute().body(), "<div class=\"news-wrap", "</a>\n</div>");
                if (html == null || html.length == 0) continue;

                htmls.add(html);
            } catch (final IOException e) {
                SingTaoRealtimeParser.LOGGER.error(e.getMessage(), e);
            }
        }

        if (htmls.isEmpty()) return Collections.emptyList();

        return htmls.stream()
            .map(Arrays::asList)
            .flatMap(Collection::stream)
            .collect(Collectors.toList())
            .stream()
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<a href=\"", SingTaoRealtimeParser.QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "<i class=\"fa fa-clock-o mr5\"></i>", SingTaoRealtimeParser.CLOSE);
                if (date == null) return null;

                try {
                    final Item item = new Item();

                    item.setUrl(url.replaceAll(Pattern.quote("instant/../instant"), "instant"));
                    item.setPublishDate(Parser.toSafeDate(SingTaoRealtimeParser.DATE_FORMAT.get().parse(date)));
                    item.setSource(this.getSource());
                    item.setCategory(category);

                    final String title = StringUtils.substringBetween(section, "<div class=\"title\">", SingTaoRealtimeParser.CLOSE);
                    if (title != null) item.setTitle(title.replace("<h1>", "").replace("</h1>", ""));

                    return item;
                } catch (final ParseException e) {
                    SingTaoRealtimeParser.LOGGER.warn(e.getMessage(), e);
                }

                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @NonNull
    @Override
    public Item getItem(@NonNull @lombok.NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new IllegalArgumentException("Item URL cannot be null");

        final String html = StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div class=\"post-content\">", "<div class=\"post-sharing\">");

        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html, "<p>", "</p>");
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce("", (description, content) -> description + content + "<br>"));

            final String[] imageContainers = StringUtils.substringsBetween(html, "<a class=\"fancybox-thumb image\"", ">");
            if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "href=\"", SingTaoRealtimeParser.QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "title=\"", SingTaoRealtimeParser.QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new)));
        }

        return item;
    }

    @Override
    public void close() {
        SingTaoRealtimeParser.DATE_FORMAT.remove();
    }
}
