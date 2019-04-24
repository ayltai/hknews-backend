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

public final class MingPaoParser extends RssParser {
    //region Constants

    private static final String DIV_CLEAR = "<div class=\"clear\"></div>";
    private static final String QUOTE     = "\"";

    //endregion

    private final Source source;

    MingPaoParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_MING_PAO);
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

        final String html = StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body(), "<hgroup>", "<div id=\"ssm2\" class=\"ssm\">");

        if (html != null) {
            item.setDescription(StringUtils.substringBetween(html, "<div id=\"upper\">", "</div>"));
            item.setDescription((item.getDescription() == null ? "" : item.getDescription() + "<br><br>") + StringUtils.substringBetween(html, "<div class=\"articlelogin\">", "</div>"));

            final String[] imageContainers = StringUtils.substringsBetween(html, "id=\"zoom_", MingPaoParser.DIV_CLEAR);
            if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
                .filter(imageContainer -> !imageContainer.startsWith("video"))
                .map(imageContainer -> {
                    final String imageUrl = StringUtils.substringBetween(imageContainer, "a href=\"", MingPaoParser.QUOTE);
                    if (imageUrl == null) return null;

                    return new Image(imageUrl, StringUtils.substringBetween(imageContainer, "dtitle=\"", MingPaoParser.QUOTE));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection((Supplier<Collection<Image>>)ArrayList::new)));

            final String[] videoContainers = StringUtils.substringsBetween(html, "id=\"zoom_video_", MingPaoParser.DIV_CLEAR);
            if (videoContainers != null) item.getVideos().addAll(Stream.of(videoContainers)
                .map(videoContainer -> {
                    final String videoUrl = StringUtils.substringBetween(videoContainer, "<a href=\"https://videop.mingpao.com/php/player1.php?file=", "&");
                    if (videoUrl == null) return null;

                    return new Video(videoUrl, videoUrl.replaceAll(".mp4", ".jpg"));
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection((Supplier<Collection<Video>>)ArrayList::new)));
        }

        return item;
    }
}
