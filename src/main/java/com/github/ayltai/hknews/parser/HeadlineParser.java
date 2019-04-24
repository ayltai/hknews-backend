package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;

public final class HeadlineParser extends RssParser {
    //region Constants

    private static final String HTTP      = "http:";
    private static final String IMAGE_URI = "http://static.stheadline.com";

    //endregion

    private final Source source;

    HeadlineParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_HEADLINE);
    }

    @NonNull
    @Override
    public Source getSource() {
        return this.source;
    }

    @NonNull
    @Override
    public Item getItem(@NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new IllegalArgumentException("Item URL cannot be null");

        final String html = this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body();

        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(html, "<div id=\"news-content\" class=\"set-font-aera\" style=\"visibility: visible;\">", "</div>");
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce("", (description, content) -> description + content));

            final String[] imageContainers = StringUtils.substringsBetween(html, "<a class=\"fancybox\" rel=\"gallery\"", "</a>");
            if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "href=\"", "\"");
                    if (imageUrl == null) return null;

                    return new Image(imageUrl.startsWith("//") ? HeadlineParser.HTTP + imageUrl : imageUrl.startsWith(HeadlineParser.HTTP) ? imageUrl : HeadlineParser.IMAGE_URI + imageUrl, StringUtils.substringBetween(imageContainer, "title=\"■", "\">"));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection((Supplier<Collection<Image>>)ArrayList::new)));
        }

        return item;
    }
}
