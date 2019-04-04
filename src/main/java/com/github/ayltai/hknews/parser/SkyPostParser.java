package com.github.ayltai.hknews.parser;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SkyPostParser extends RssParser {
    //region Constants

    private static final String CLOSE_HEADER = "</h3>";
    private static final String BREAK        = "<br>";
    private static final String OPEN_TITLE   = "<h4>";
    private static final String CLOSE_TITLE  = "</h4>";

    //endregion

    private final Source source;

    SkyPostParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_SKYPOST);
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

        final String url  = item.getUrl().replaceAll("%", "%25");
        final String html = StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(url).execute().body(), "<!-- article details page title -->", "<div class=\"article-detail_extra-info\">");

        if (html != null) {
            final String headline = StringUtils.substringBetween(html, "<h3 class=\"article-details-title__lower-title\">", SkyPostParser.CLOSE_HEADER);
            item.setDescription(headline == null || headline.isEmpty() ? "" : SkyPostParser.OPEN_TITLE + headline + SkyPostParser.CLOSE_TITLE + SkyPostParser.BREAK);

            final String[] descriptions = StringUtils.substringsBetween(html, "<P>", "</P>");
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce(item.getDescription(), (description, content) -> {
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
                .collect(Collectors.toCollection((Supplier<Collection<Image>>)ArrayList::new)));
        }

        return item;
    }
}
