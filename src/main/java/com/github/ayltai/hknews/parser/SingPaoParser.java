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

public final class SingPaoParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(SingPaoParser.class);

    //region Constants

    private static final String BASE_URI  = "https://www.singpao.com.hk/";
    private static final String QUOTE     = "'";
    private static final String FONT      = "</font>";
    private static final String PARAGRAPH = "</p>";

    private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

    //endregion

    @Getter
    private final Source source;

    SingPaoParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_SING_PAO);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull @lombok.NonNull final Category category) {
        if (category.getUrls().isEmpty()) return Collections.emptyList();

        return category.getUrls()
            .stream()
            .map(url -> {
                try {
                    return StringUtils.substringsBetween(this.apiServiceFactory.create().getHtml(url).execute().body(), "<tr valign='top'><td width='220'>", "</td></tr>");
                } catch (final IOException e) {
                    SingPaoParser.LOGGER.error(e.getMessage(), e);

                    return null;
                }
            })
            .filter(Objects::nonNull)
            .map(Arrays::asList)
            .collect((Supplier<List<String>>)ArrayList::new, List::addAll, List::addAll)
            .stream()
            .map(section -> {
                final String url = StringUtils.substringBetween(section, "<td><a href='", SingPaoParser.QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "<font class='list_date'>", "<br>");
                if (date == null) return null;

                try {
                    final Item item = new Item();

                    item.setTitle(StringUtils.substringBetween(section, "class='list_title'>", "</a>"));
                    item.setDescription(StringUtils.substringBetween(section, "<br><br>\n", SingPaoParser.FONT));
                    item.setUrl(SingPaoParser.BASE_URI + url);
                    item.setPublishDate(Parser.toSafeDate(SingPaoParser.DATE_FORMAT.get().parse(date)));
                    item.setSource(this.getSource());
                    item.setCategory(category);

                    return item;
                } catch (final ParseException e) {
                    SingPaoParser.LOGGER.warn(e.getMessage(), e);
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

        final String html = StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<td class='news_title'>", "您可能有興趣:");

        if (html != null) {
            String[] descriptions = StringUtils.substringsBetween(html, "<p class=\"內文\">", SingPaoParser.PARAGRAPH);
            if (descriptions == null) descriptions = StringUtils.substringsBetween(html, "<p>", SingPaoParser.PARAGRAPH);
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce("", (description, content) -> description + content.trim() + "<br><br>"));

            final String[] imageUrls         = StringUtils.substringsBetween(html, "target='_blank'><img src='", SingPaoParser.QUOTE);
            final String[] imageDescriptions = StringUtils.substringsBetween(html, "<font size='4'>", SingPaoParser.FONT);

            if (imageUrls != null && imageUrls.length > 0) {
                for (int i = 0; i < imageUrls.length; i++) item.getImages().add(new Image(SingPaoParser.BASE_URI + imageUrls[i], imageDescriptions == null ? null : imageDescriptions.length > i ? imageDescriptions[i] : null));
            }

            item.setImages(item.getImages()
                .stream()
                .distinct()
                .collect(Collectors.toList()));
        }

        return item;
    }
}
