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
import java.util.Locale;
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
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;
import lombok.Getter;

public final class TheStandardParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(TheStandardParser.class);

    //region Constants

    private static final String BASE_URL        = "http://www.thestandard.com.hk/";
    private static final String CLOSE_QUOTE     = "\"";
    private static final String OPEN_HREF       = "<a href=\"";
    private static final String OPEN_PARAGRAPH  = "<p>";
    private static final String CLOSE_PARAGRAPH = "</p>";

    private static final String FORMAT_LONG  = "dd MMM yyyy h:mm a";
    private static final String FORMAT_SHORT = "dd MMM yyyy";

    private static final ThreadLocal<DateFormat> DATE_FORMAT_LONG  = ThreadLocal.withInitial(() -> new SimpleDateFormat(TheStandardParser.FORMAT_LONG, Locale.ENGLISH));
    private static final ThreadLocal<DateFormat> DATE_FORMAT_SHORT = ThreadLocal.withInitial(() -> new SimpleDateFormat(TheStandardParser.FORMAT_SHORT, Locale.ENGLISH));

    //endregion

    @Getter
    private final Source source;

    TheStandardParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository) {
        super(apiServiceFactory, sourceRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_THE_STANDARD);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull @lombok.NonNull final Category category) {
        if (category.getUrls().isEmpty()) return Collections.emptyList();

        final List<String[]> htmls = new ArrayList<>();
        for (final String url : category.getUrls()) {
            final String[] tokens = url.split(Pattern.quote("?"));

            try {
                final String[] html = StringUtils.substringsBetween(this.apiServiceFactory.create().postHtml(tokens[0], Integer.parseInt(tokens[1].split("=")[1]), 1).execute().body(), "<li class='caption'>", "</li>");
                if (html == null || html.length == 0) continue;

                htmls.add(html);
            } catch (final IOException e) {
                TheStandardParser.LOGGER.error(e.getMessage(), e);
            }
        }

        if (htmls.isEmpty()) return Collections.emptyList();

        return htmls.stream()
            .map(Arrays::asList)
            .flatMap(Collection::stream)
            .collect(Collectors.toList())
            .stream()
            .map(section -> {
                final String url = StringUtils.substringBetween(section, TheStandardParser.OPEN_HREF, TheStandardParser.CLOSE_QUOTE);
                if (url == null) return null;

                final String date = StringUtils.substringBetween(section, "<span>", "</span>");
                if (date == null) return null;

                try {
                    final Item item = new Item();

                    item.setTitle(StringUtils.substringBetween(StringUtils.substringBetween(section, "<h1>", "</h1>"), "\">", "</a>"));
                    item.setDescription(StringUtils.substringBetween(section, TheStandardParser.OPEN_PARAGRAPH, TheStandardParser.CLOSE_PARAGRAPH));
                    item.setUrl(TheStandardParser.BASE_URL + url);
                    item.setPublishDate(Parser.toSafeDate((date.length() > TheStandardParser.FORMAT_SHORT.length() ? TheStandardParser.DATE_FORMAT_LONG : TheStandardParser.DATE_FORMAT_SHORT).get().parse(date)));
                    item.setSource(this.getSource());
                    item.setCategory(category);

                    return item;
                } catch (final ParseException e) {
                    TheStandardParser.LOGGER.warn(e.getMessage(), e);
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

        final String html = StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div class=\"content\">", "<div class=\"related\">");

        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html, TheStandardParser.OPEN_PARAGRAPH, TheStandardParser.CLOSE_PARAGRAPH);
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce("", (description, content) -> description + content.trim() + "<br><br>"));

            final String[] imageContainers = StringUtils.substringsBetween(html, "<figure>", "</figure>");
            if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, TheStandardParser.OPEN_HREF, TheStandardParser.CLOSE_QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "<i>", "</i>"));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new)));
        }

        return item;
    }

    @Override
    public void close() {
        TheStandardParser.DATE_FORMAT_LONG.remove();
        TheStandardParser.DATE_FORMAT_SHORT.remove();
    }
}
