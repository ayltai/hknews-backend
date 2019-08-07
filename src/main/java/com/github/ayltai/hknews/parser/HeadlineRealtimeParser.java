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

public final class HeadlineRealtimeParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(HeadlineRealtimeParser.class);

    //region Constants

    private static final String BASE_URI  = "http://hd.stheadline.com";
    private static final String IMAGE_URI = "http://static.stheadline.com";
    private static final String LINK      = "</a>";
    private static final String QUOTE     = "\"";
    private static final String HTTP      = "http:";

    private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm"));

    //endregion

    private final Source source;

    HeadlineRealtimeParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_HEADLINE_REALTIME);
    }

    @NonNull
    @Override
    public Source getSource() {
        return this.source;
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull @lombok.NonNull final Category category) {
        if (category.getUrls().isEmpty()) return Collections.emptyList();

        return category.getUrls()
            .stream()
            .map(url -> {
                try {
                    return StringUtils.substringsBetween(this.apiServiceFactory.create().getHtml(url).execute().body(), "<div class=\"topic\">", "<p class=\"text-left\">");
                } catch (final IOException e) {
                    HeadlineRealtimeParser.LOGGER.error(e.getMessage(), e);

                    return null;
                }
            })
            .filter(Objects::nonNull)
            .map(Arrays::asList)
            .collect((Supplier<List<String>>)ArrayList::new, List::addAll, List::addAll)
            .stream()
            .map(section -> {
                final String date = StringUtils.substringBetween(section, "<i class=\"fa fa-clock-o\"></i>", "</span>");
                if (date == null) return null;

                final String title = StringUtils.substringBetween(section, "<h", "</h");

                try {
                    final Item item = new Item();

                    item.setTitle(StringUtils.substringBetween(title, "\">", HeadlineRealtimeParser.LINK));
                    item.setDescription(StringUtils.substringBetween(section, "<p class=\"text\">", "</p>"));
                    item.setUrl(title == null ? HeadlineRealtimeParser.BASE_URI : HeadlineRealtimeParser.BASE_URI + StringUtils.substringBetween(title, "<a href=\"", "\" "));
                    item.setPublishDate(Parser.toSafeDate(HeadlineRealtimeParser.DATE_FORMAT.get().parse(date)));
                    item.setSource(this.getSource());
                    item.setCategory(category);

                    return item;
                } catch (final ParseException e) {
                    HeadlineRealtimeParser.LOGGER.warn(e.getMessage(), e);

                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection((Supplier<Collection<Item>>)ArrayList::new));
    }

    @NonNull
    @Override
    public Item getItem(@NonNull @lombok.NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new IllegalArgumentException("Item URL cannot be null");

        final String html = this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body();

        if (html != null) {
            item.setDescription(StringUtils.substringBetween(html, "<div id=\"news-content\" class=\"set-font-aera\" style=\"visibility: visible;\">", "</div>"));

            final String[] imageContainers = StringUtils.substringsBetween(html, "<a class=\"fancybox image\" rel=\"fancybox-thumb\"", HeadlineRealtimeParser.LINK);
            if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "href=\"", HeadlineRealtimeParser.QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl.startsWith("//") ? HeadlineRealtimeParser.HTTP + imageUrl : imageUrl.startsWith(HeadlineRealtimeParser.HTTP) ? imageUrl : HeadlineRealtimeParser.IMAGE_URI + imageUrl, StringUtils.substringBetween(imageContainer, "title=\"", HeadlineRealtimeParser.QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection((Supplier<Collection<Image>>)ArrayList::new)));
        }

        return item;
    }
}
