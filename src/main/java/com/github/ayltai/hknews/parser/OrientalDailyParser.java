package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import io.micrometer.core.annotation.Timed;

public final class OrientalDailyParser extends RssParser {
    //region Constants

    private static final String BASE_URI = "http://orientaldaily.on.cc";
    private static final String QUOTE    = "\"";
    private static final String SLASH    = "/";
    private static final String CLOSE    = "</div>";

    private static final int MEDIA_ID_POSITION = 4;
    private static final int VIDEO_ID_POSITION = 6;

    //endregion

    private final Source source;

    OrientalDailyParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_ORIENTAL_DAILY);
    }

    @NonNull
    @Override
    public Source getSource() {
        return this.source;
    }

    @NonNull
    @Timed(
        value     = "parser_get_item",
        extraTags = { "orientaldaily" }
    )
    @Override
    public Item getItem(@NonNull @lombok.NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new NullPointerException("Item URL cannot be null");

        final String html = StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<div id=\"contentCTN-top\"", "<div id=\"articleNav\">");

        if (html != null) {
            String[] descriptions = StringUtils.substringsBetween(html, "<p>", "</p>");
            if (descriptions != null) {
                descriptions = Arrays.copyOf(descriptions, descriptions.length - 1);
                item.setDescription(Stream.of(descriptions)
                    .reduce("", (description, content) -> description + content + "<br><br>"));
            }

            final String[] imageContainers = StringUtils.substringsBetween(html, "<div class=\"photo", OrientalDailyParser.CLOSE);
            if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "href=\"", OrientalDailyParser.QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(OrientalDailyParser.BASE_URI + imageUrl, StringUtils.substringBetween(imageContainer, "title=\"", OrientalDailyParser.QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection((Supplier<Collection<Image>>)ArrayList::new)));

            final String date = StringUtils.substringBetween(item.getUrl(), "http://orientaldaily.on.cc/cnt/news/", OrientalDailyParser.SLASH);
            if (date != null) {
                final String[] videoContainers = StringUtils.substringsBetween(this.apiServiceFactory.create().getHtml("http://orientaldaily.on.cc/cnt/keyinfo/" + date + "/videolist.xml").execute().body(), "<news>", "</news>");
                if (videoContainers != null) item.getVideos().addAll(Stream.of(videoContainers)
                    .map(videoContainer -> OrientalDailyParser.extractVideo(item.getUrl(), date, videoContainer))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toCollection((Supplier<Collection<Video>>)ArrayList::new)));
            }
        }

        return item;
    }

    private static Video extractVideo(@NonNull @lombok.NonNull final String url, @NonNull @lombok.NonNull final String date, @NonNull @lombok.NonNull final String videoContainer) {
        final String link = StringUtils.substringBetween(url, date + OrientalDailyParser.SLASH, ".html");
        if (link == null) return null;

        if (("odn-" + date + "-" + date.substring(OrientalDailyParser.MEDIA_ID_POSITION) + "_" + link).equals(StringUtils.substringBetween(videoContainer, "<articleID>", "</articleID>"))) {
            final String videoUrl = StringUtils.substringBetween(videoContainer, "?mid=", "&amp;mtype=video");
            final String imageUrl = StringUtils.substringBetween(videoContainer, "<thumbnail>", "</thumbnail>");

            if (videoUrl == null || imageUrl == null) return null;

            return new Video("http://video.cdn.on.cc/Video/" + date.substring(0, OrientalDailyParser.VIDEO_ID_POSITION) + OrientalDailyParser.SLASH + videoUrl + "_ipad.mp4", "http://tv.on.cc/xml/Thumbnail/" + date.substring(0, 6) + "/bigthumbnail/" + imageUrl);
        }

        return null;
    }
}
