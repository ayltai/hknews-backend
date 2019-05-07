package com.github.ayltai.hknews.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Image;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.model.Video;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;

public final class ScmpParser extends RssParser {
    //region Constants

    private static final String JSON_PREFIX   = "{\"type\":\"json\"";
    private static final String JSON_TYPE     = "type";
    private static final String JSON_CHILDREN = "children";
    private static final String JSON_ATTRIBS  = "attribs";
    private static final String DIV           = "</div>";
    private static final String QUOTE         = "\"";

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
    public Item getItem(@NonNull @lombok.NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new IllegalArgumentException("Item URL cannot be null");

        final String html = this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body();

        if (html != null) {
            final String image = StringUtils.substringBetween(html, "images.0\":", ",\"isSlideshow\"");
            if (image != null) item.getImages().add(new Image(StringUtils.substringBetween("\"url\":\"", ScmpParser.QUOTE), StringUtils.substringBetween("{\"title\":\"", ScmpParser.QUOTE)));

            final String mainContent = StringUtils.substringBetween(html, ScmpParser.JSON_PREFIX, ",\"sections\"");
            if (mainContent == null) return item;

            final JSONArray elements = new JSONObject(ScmpParser.JSON_PREFIX + mainContent).getJSONArray("json");

            ScmpParser.extractDescriptions(elements, item);
            ScmpParser.extractImages(elements, item);
            ScmpParser.extractVideos(elements, item);
        }

        return item;
    }

    private static void extractDescriptions(@NonNull @lombok.NonNull final JSONArray elements, @NonNull @lombok.NonNull final Item item) throws JSONException {
        for (int i = 0; i < elements.length(); i++) {
            final JSONObject json = elements.getJSONObject(i);
            if ("p".equals(json.getString(ScmpParser.JSON_TYPE)) || "span".equals(json.getString(ScmpParser.JSON_TYPE))) {
                final JSONArray array = json.getJSONArray(ScmpParser.JSON_CHILDREN);
                for (int j = 0; j < array.length(); j++) {
                    final JSONObject node = array.getJSONObject(j);
                    if ("text".equals(node.optString(ScmpParser.JSON_TYPE))) {
                        item.setDescription((item.getDescription() == null ? "" : item.getDescription()) + node.optString("data") + "<br><br>");
                    } else {
                        final JSONArray child = new JSONArray();
                        child.put(node);

                        ScmpParser.extractDescriptions(child, item);
                    }
                }
            }
        }
    }

    private static void extractImages(@NonNull @lombok.NonNull final JSONArray elements, @NonNull @lombok.NonNull final Item item) throws JSONException {
        for (int i = 0; i < elements.length(); i++) {
            final JSONObject json = elements.getJSONObject(i);
            if ("p".equals(json.getString(ScmpParser.JSON_TYPE))) {
                final JSONArray array = json.getJSONArray(ScmpParser.JSON_CHILDREN);
                for (int j = 0; j < array.length(); j++) {
                    final JSONObject node = array.getJSONObject(j);
                    if ("img".equals(node.optString(ScmpParser.JSON_TYPE))) {
                        final JSONObject attribs = node.getJSONObject(ScmpParser.JSON_ATTRIBS);
                        item.getImages().add(new Image(attribs.optString("src"), attribs.optString("title")));
                    }
                }
            }
        }
    }

    private static void extractVideos(@NonNull @lombok.NonNull final JSONArray elements, @NonNull @lombok.NonNull final Item item) throws JSONException {
        for (int i = 0; i < elements.length(); i++) {
            final JSONObject json = elements.getJSONObject(i);
            if ("div".equals(json.getString(ScmpParser.JSON_TYPE))) {
                final JSONArray array = json.getJSONArray(ScmpParser.JSON_CHILDREN);
                for (int j = 0; j < array.length(); j++) {
                    final String videoId = array.getJSONObject(j).optString("video_id");
                    if (videoId != null) item.getVideos().add(new Video("https://cf.cdn.vid.ly/" + videoId + "/hd_mp4.mp4", array.getJSONObject(j).getJSONObject(ScmpParser.JSON_ATTRIBS).optString("data-poster")));
                }
            }
        }
    }
}
