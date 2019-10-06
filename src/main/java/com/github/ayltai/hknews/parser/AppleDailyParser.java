package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;

import lombok.Getter;

public final class AppleDailyParser extends Parser {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppleDailyParser.class);

    //region Constants

    private static final String SLASH        = "/";
    private static final String QUOTE        = "\"";
    private static final String HREF         = "href=\"";
    private static final String TITLE        = "title=\"";
    private static final String DIV          = "</div>";
    private static final String OPEN_HEADER  = "<h3>";
    private static final String CLOSE_HEADER = "</h3>";
    private static final String TYPE         = "type";
    private static final String URL          = "url";
    private static final String PROMO_ITEMS  = "promo_items";
    private static final String PROMO_IMAGE  = "promo_image";

    private static final long SECOND = 1000;

    private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

    //endregion

    @Getter
    private final Source source;

    AppleDailyParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_APPLE_DAILY);
    }

    @NonNull
    @Override
    public Collection<Item> getItems(@NonNull @lombok.NonNull final Category category) {
        if (category.getUrls().isEmpty()) return Collections.emptyList();

        return category.getUrls()
            .stream()
            .map(url -> {
                try {
                    return StringUtils.substringsBetween(StringUtils.substringBetween(this.apiServiceFactory.create().getHtml(url.replaceAll(Pattern.quote("{}"), AppleDailyParser.DATE_FORMAT.get().format(new Date()))).execute().body(), "<div class=\"itemContainer\"", "<div class=\"clear\"></div>"), "<div class=\"item\">", AppleDailyParser.DIV);
                } catch (final IOException e) {
                    AppleDailyParser.LOGGER.error(this.getClass().getSimpleName(), e.getMessage(), e);

                    return null;
                }
            })
            .filter(Objects::nonNull)
            .map(Arrays::asList)
            .flatMap(Collection::stream)
            .collect(Collectors.toList())
            .stream()
            .map(section -> {
                final String url  = StringUtils.substringBetween(section, AppleDailyParser.HREF, AppleDailyParser.QUOTE);
                final String time = StringUtils.substringBetween(section, "pix/", "_");

                if (url == null || time == null) return null;

                final Item item = new Item();

                item.setTitle(StringUtils.substringBetween(section, AppleDailyParser.TITLE, AppleDailyParser.QUOTE));
                item.setUrl(url.substring(0, url.lastIndexOf(AppleDailyParser.SLASH)).replace("video", "news").replace("actionnews/local", "local/daily/article").replace("actionnews/international", "international/daily/article").replace("actionnews/finance", "finance/daily/article").replace("actionnews/entertainment", "entertainment/daily/article").replace("actionnews/sports", "sports/daily/article"));
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

        if (html == null) return this.getItem(item, fullHtml);

        final String[] descriptions = StringUtils.substringsBetween(html, "<div class=\"ArticleContent_Inner\">", AppleDailyParser.DIV);
        if (descriptions != null) item.setDescription(Stream.of(descriptions)
            .reduce("", (description, content) -> description + content.trim().replace("\n", "").replace("\t", "").replace("<h2>", AppleDailyParser.OPEN_HEADER).replace("</h2>", AppleDailyParser.CLOSE_HEADER)));

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

    private Item getItem(@NonNull @lombok.NonNull final Item item, @NonNull @lombok.NonNull final String html) {
        final JSONObject json     = new JSONObject(StringUtils.substringBetween(html, "Fusion.globalContent=", "};") + "}");
        final JSONArray  contents = json.getJSONArray("content_elements");

        for (int i = 0; i < contents.length(); i++) {
            final JSONObject content = contents.getJSONObject(i);
            final String     type    = content.getString(AppleDailyParser.TYPE);

            if ("text".equals(type)) {
                item.setDescription((item.getDescription() == null ? "" : item.getDescription()) + content.getString("content"));
            } else if ("image".equals(type)) {
                final Image image = new Image();
                image.setDescription(content.getString("caption"));
                image.setImageUrl(content.getString(AppleDailyParser.URL));

                item.getImages().add(image);
            }
        }

        if (json.has(AppleDailyParser.PROMO_ITEMS)) {
            final JSONObject promoItem = json.getJSONObject(AppleDailyParser.PROMO_ITEMS).getJSONObject("basic");
            if ("video".equals(promoItem.getString(AppleDailyParser.TYPE))) {
                if (promoItem.has(AppleDailyParser.PROMO_IMAGE)) {
                    final Video video = new Video();
                    video.setImageUrl(promoItem.getJSONObject(AppleDailyParser.PROMO_IMAGE).getString(AppleDailyParser.URL));
                    video.setVideoUrl(promoItem.getJSONArray("streams").getJSONObject(0).getString(AppleDailyParser.URL));

                    item.getVideos().add(video);
                }
            }
        }

        return item;
    }

    @Override
    public void close() {
        AppleDailyParser.DATE_FORMAT.remove();
    }
}
