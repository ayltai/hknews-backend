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

public final class SkyPostParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkyPostParser.class.getSimpleName());

    //region Constants

    private static final String BREAK        = "<br>";
    private static final String OPEN_TITLE   = "<h4>";
    private static final String CLOSE_TITLE  = "</h4>";

    private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy/MM/dd"));

    //endregion

    @Getter
    private final Source source;

    SkyPostParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_SKYPOST);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull @lombok.NonNull final Category category) {
        if (category.getUrls().isEmpty()) return Collections.emptyList();

        final List<String[]> htmls = new ArrayList<>();
        for (final String url : category.getUrls()) {
            try {
                final String[] html = StringUtils.substringsBetween(StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(url).execute().body(), "<section class=\"article-listing", "</section>"), "<h5 class='card-title'>", "<button class=\"share-container\"");
                if (html == null || html.length == 0) continue;

                htmls.add(html);
            } catch (final IOException e) {
                SkyPostParser.LOGGER.error(e.getMessage(), e);
            }
        }

        if (htmls.isEmpty()) return Collections.emptyList();

        return htmls.stream()
            .map(Arrays::asList)
            .flatMap(Collection::stream)
            .collect(Collectors.toList())
            .stream()
            .map(section -> {
                final String url = StringUtils.substringBetween(section , "<a href='", "'>");
                if (url == null) return null;

                final String[] dates = StringUtils.substringsBetween(section, "<span class='time'>", "</span>");
                if (dates == null) return null;

                try {
                    final Item item = new Item();

                    item.setTitle(StringUtils.substringBetween(section, "'>", "</a>"));
                    item.setUrl(url);
                    item.setSource(this.getSource());
                    item.setCategory(category);
                    item.setPublishDate(Parser.toSafeDate(SkyPostParser.DATE_FORMAT.get().parse(dates[1])));

                    return item;
                } catch (final ParseException e) {
                    SkyPostParser.LOGGER.warn("Invalid date format: " + dates[1], e);
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

        final String html = StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<section class=\"article-head\">", "<div class=\"article-detail_extra-info\">");

        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html, "<P>", "</P>");
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce("", (description, content) -> {
                    String text = StringUtils.substringBetween(content, "<b>", "</b>");
                    if (text == null) text = StringUtils.substringBetween(content, "<B>", "</B>");

                    return text == null || text.isEmpty() ? description + content + SkyPostParser.BREAK : description + SkyPostParser.OPEN_TITLE + text + SkyPostParser.CLOSE_TITLE + SkyPostParser.BREAK;
                }));

            final String[] imageContainers = StringUtils.substringsBetween(html, "<p class=\"article-details-img-container\">", "</div>");
            if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "data-src=\"", "\"");
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "<p class=\"article-details-img-caption\">", "</p>"));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new)));
        }

        return item;
    }

    @Override
    public void close() {
        SkyPostParser.DATE_FORMAT.remove();
    }
}
