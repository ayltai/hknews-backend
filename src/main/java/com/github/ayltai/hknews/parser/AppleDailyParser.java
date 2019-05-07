package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONObject;
import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;

public final class AppleDailyParser extends Parser {
    //region Constants

    private static final String SLASH        = "/";
    private static final String QUOTE        = "\"";
    private static final String HREF         = "href=\"";
    private static final String TITLE        = "title=\"";
    private static final String DIV          = "</div>";
    private static final String OPEN_HEADER  = "<h3>";
    private static final String CLOSE_HEADER = "</h3>";

    private static final long SECOND = 1000;

    private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

    //endregion

    private final Source source;

    AppleDailyParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_APPLE_DAILY);
    }

    @NonNull
    @Override
    public Source getSource() {
        return this.source;
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull @lombok.NonNull final Category category) throws IOException {
        if (category.getUrl() == null) return Collections.emptyList();

        final String[] sections = StringUtils.substringsBetween(StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(category.getUrl().replaceAll(Pattern.quote("{}"), AppleDailyParser.DATE_FORMAT.get().format(new Date()))).execute().body(), "<div class=\"itemContainer\"", "<div class=\"clear\"></div>"), "<div class=\"item\">", AppleDailyParser.DIV);
        if (sections == null) return Collections.emptyList();

        return Stream.of(sections)
            .map(section -> {
                final String url  = StringUtils.substringBetween(section, AppleDailyParser.HREF, AppleDailyParser.QUOTE);
                final String time = StringUtils.substringBetween(section, "pix/", "_");

                if (url == null || time == null) return null;

                final Item item = new Item();

                item.setTitle(StringUtils.substringBetween(section, AppleDailyParser.TITLE, AppleDailyParser.QUOTE));
                item.setUrl(url.substring(0, url.lastIndexOf(AppleDailyParser.SLASH)).replaceAll("video", "news").replaceAll("actionnews/local", "local/daily/article").replaceAll("actionnews/international", "international/daily/article").replaceAll("actionnews/finance", "finance/daily/article").replaceAll("actionnews/entertainment", "entertainment/daily/article").replaceAll("actionnews/sports", "sports/daily/article"));
                item.setPublishDate(Parser.toSafeDate(new Date(Long.parseLong(time) * AppleDailyParser.SECOND)));
                item.setSource(this.getSource());
                item.setCategory(category);

                return item;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection((Supplier<Collection<Item>>)ArrayList::new));

    }

    @NonNull
    @Override
    public Item getItem(@NonNull @lombok.NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new IllegalArgumentException("Item URL cannot be null");

        final String fullHtml = this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body();
        final String html     = StringUtils.substringBetween(fullHtml, "<!-- START ARTILCLE CONTENT -->", "<!-- END ARTILCLE CONTENT -->");

        final String[] descriptions = StringUtils.substringsBetween(html, "<div class=\"ArticleContent_Inner\">", AppleDailyParser.DIV);
        if (descriptions != null) item.setDescription(Stream.of(descriptions)
            .reduce("", (description, content) -> description + content.trim().replaceAll("\n", "").replaceAll("\t", "").replaceAll("<h2>", AppleDailyParser.OPEN_HEADER).replaceAll("</h2>", AppleDailyParser.CLOSE_HEADER)));

        item.setDescription(item.getDescription().replace("<img src=\"https://staticlayout.appledaily.hk/web_images/layout/art_end.gif\" />", ""));

        final String[] imageContainers = StringUtils.substringsBetween(html, "rel=\"fancybox-button\"", "/>");
        if (imageContainers != null) item.getImages().addAll(Stream.of(imageContainers)
            .map(imageContainer -> {
                final String imageUrl = StringUtils.substringBetween(imageContainer, AppleDailyParser.HREF, AppleDailyParser.QUOTE);
                if (imageUrl == null) return null;

                return new Image(imageUrl, StringUtils.substringBetween(imageContainer, AppleDailyParser.TITLE, AppleDailyParser.QUOTE));
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection((Supplier<Collection<Image>>)ArrayList::new)));

        final String videoUrl = StringUtils.substringBetween(fullHtml, "var videoUrl = '", "';");

        if (videoUrl != null) {
            final String ngs = StringUtils.substringBetween(fullHtml, "var ngsobj = ", ";");
            if (ngs != null) item.getVideos().add(new Video(videoUrl, new JSONObject(ngs).optString("ngs_thumbnail")));
        }

        return item;
    }
}
