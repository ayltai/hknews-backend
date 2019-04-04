package com.github.ayltai.hknews.parser;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
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

public final class ScmpParser extends RssParser {
    //region Constants

    private static final String DIV   = "</div>";
    private static final String QUOTE = "\"";

    //endregion

    private final Source source;

    ScmpParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_SCMP);
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
            final String mainContent = StringUtils.substringBetween(html, "<div class=\"panel-pane pane-entity-field pane-node-body", "</div>\n</div>");
            if (mainContent == null) return item;

            final String videoContainer = StringUtils.substringBetween(mainContent, "<iframe ", "</iframe>");
            if (videoContainer != null) {
                final String videoUrl = StringUtils.substringBetween(videoContainer, "id=", ScmpParser.QUOTE);
                if (videoUrl != null) item.getVideos().add(new Video("https://cf.cdn.vid.ly/" + videoUrl + "/mp4.mp4", "https://vid.ly/" + videoUrl + "/poster_hd"));
            }

            final String[] imageContainers = StringUtils.substringsBetween(StringUtils.substringBetween(html, "<div class=\"swiper-container scmp-gallery-swiper\">", ScmpParser.DIV), "<img ", "/>");
            if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "data-enlarge=\"", ScmpParser.QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "data-caption=\"", ScmpParser.QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection((Supplier<Collection<Image>>)ArrayList::new)));

            final String[] descriptions = StringUtils.substringsBetween(mainContent, "<p>", "</p>");
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce("", (description, content) -> {
                    final String imageUrl = StringUtils.substringBetween(content, "data-original=\"", ScmpParser.QUOTE);
                    if (imageUrl == null) {
                        return description + content + "<br><br>";
                    }

                    item.getImages().add(new Image(imageUrl, StringUtils.substringBetween(content, "<img title=\"", ScmpParser.QUOTE)));

                    return description;
                }));
        }

        return item;
    }
}
