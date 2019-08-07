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
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;

public final class HketParser extends RssParser {
    //region Constants

    private static final String BASE_URI_PAPER = "http://paper.hket.com/";
    private static final String QUOTE          = "\"";

    //endregion

    private final Source source;

    HketParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_HKET);
    }

    @NonNull
    @Override
    public Source getSource() {
        return this.source;
    }

    @NonNull
    @Override
    public Item getItem(@NonNull @lombok.NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new IllegalArgumentException("Item URL cannot be null");

        final String html = StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div class=\"article-detail\">", "<div class=\"readAll-btn-container\">");

        if (html != null) {
            final boolean isPaperNews = item.getUrl().contains(HketParser.BASE_URI_PAPER);

            final String[] descriptions = StringUtils.substringsBetween(html, isPaperNews ? "<P>" : "<p>", isPaperNews ? "</P>" : "</p>");
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce("", (description, content) -> description + content + "<br>"));

            final String[] imageContainers = StringUtils.substringsBetween(html, "<img ", "/>");
            if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
                .map(HketParser::extractImage)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection((Supplier<Collection<Image>>)ArrayList::new)));

            final String videoId = StringUtils.substringBetween(html, " src=\"//www.youtube.com/embed/", "?rel=0");
            if (videoId != null) item.getVideos().add(new Video("https://www.youtube.com/watch?v=" + videoId, "https://img.youtube.com/vi/" + videoId + "/mqdefault.jpg"));
        }

        return item;
    }

    private static Image extractImage(@NonNull @lombok.NonNull final String imageContainer) {
        final String imageUrl = StringUtils.substringBetween(imageContainer, "data-src=\"", HketParser.QUOTE);
        if (imageUrl == null) return null;

        return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "data-alt=\"", HketParser.QUOTE));
    }
}
