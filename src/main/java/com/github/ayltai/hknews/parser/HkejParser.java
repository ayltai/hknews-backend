package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;
import lombok.Getter;

public final class HkejParser extends RssParser {
    //region Constants

    private static final String QUOTE = "\"";
    private static final String HTTP  = "http:";
    private static final String HTTPS = "https:";

    //endregion

    @Getter
    private final Source source;

    HkejParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_HKEJ);
    }

    @NonNull
    @Override
    public Item getItem(@NonNull @lombok.NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new IllegalArgumentException("Item URL cannot be null");

        final String html = this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body();

        if (html != null) {
            final String[] descriptions = StringUtils.substringsBetween(StringUtils.substringBetween(html, "<div id='article-content'>", "</div>"), ">", "<");
            if (descriptions != null) item.setDescription(Stream.of(descriptions)
                .reduce("", (description, content) -> description + content.trim() + "<br>"));

            final String imageContainer = StringUtils.substringBetween(html, "<span class='enlargeImg'>", "</span>");
            if (imageContainer != null) {
                final String imageUrl = StringUtils.substringBetween(imageContainer, "<a href=\"", HkejParser.QUOTE);
                if (imageUrl != null) item.getImages().add(new Image(imageUrl.startsWith(HkejParser.HTTP) || imageUrl.startsWith(HkejParser.HTTPS) ? imageUrl : HkejParser.HTTP + imageUrl, StringUtils.substringBetween(imageContainer, "title=\"", HkejParser.QUOTE)));
            }
        }

        return item;
    }
}
