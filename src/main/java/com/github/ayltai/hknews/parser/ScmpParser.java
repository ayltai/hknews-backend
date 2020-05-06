package com.github.ayltai.hknews.parser;

import java.io.IOException;

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
import lombok.Getter;

public final class ScmpParser extends RssParser {
    //region Constants

    private static final String JSON_PREFIX   = "}]})\":{\"type\":\"json\"";
    private static final String JSON_TYPE     = "type";
    private static final String JSON_CHILDREN = "children";
    private static final String JSON_ATTRIBS  = "attribs";
    private static final String QUOTE         = "\"";

    //endregion

    @Getter
    private final Source source;

    ScmpParser(@NonNull @lombok.NonNull final ApiServiceFactory apiServiceFactory, @NonNull @lombok.NonNull final SourceRepository sourceRepository, @NonNull @lombok.NonNull final ItemRepository itemRepository) {
        super(apiServiceFactory, sourceRepository, itemRepository);

        this.source = sourceRepository.findByName(ParserFactory.SOURCE_SCMP);
    }

    @NonNull
    @Override
    public Item getItem(@NonNull @lombok.NonNull final Item item) throws IOException {
        if (item.getUrl() == null) throw new IllegalArgumentException("Item URL cannot be null");

        final String html = this.apiServiceFactory.create().getHtml(item.getUrl()).execute().body();

        if (html != null) {
            final String image = StringUtils.substringBetween(html, "images.0\":{\"title\"", ",\"isSlideshow\"");
            if (image != null) item.getImages().add(new Image(StringUtils.substringBetween(image, "\"url\":\"", ScmpParser.QUOTE), StringUtils.substringBetween("{\"title\"" + image, "{\"title\":\"", ScmpParser.QUOTE)));

            final String mainContent = StringUtils.substringBetween(html, ScmpParser.JSON_PREFIX, ",\"sections\"");
            if (mainContent == null) return item;

            final JSONArray elements = new JSONObject("{\"type\":\"json\"" + mainContent).getJSONArray("json");

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
                if (!json.has(ScmpParser.JSON_CHILDREN)) continue;

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
            if ("p".equals(json.getString(ScmpParser.JSON_TYPE)) || "a".equals(json.getString(ScmpParser.JSON_TYPE))) {
                if (!json.has(ScmpParser.JSON_CHILDREN)) continue;

                final JSONArray array = json.getJSONArray(ScmpParser.JSON_CHILDREN);
                for (int j = 0; j < array.length(); j++) {
                    final JSONObject node = array.getJSONObject(j);
                    if ("img".equals(node.optString(ScmpParser.JSON_TYPE))) {
                        final JSONObject attribs = node.getJSONObject(ScmpParser.JSON_ATTRIBS);
                        item.getImages().add(new Image(attribs.optString("src"), attribs.optString("title")));
                    } else if ("a".equals(node.getString(ScmpParser.JSON_TYPE))) {
                        final JSONArray image = new JSONArray();
                        image.put(node);

                        ScmpParser.extractImages(image, item);
                    }
                }
            }
        }
    }

    private static void extractVideos(@NonNull @lombok.NonNull final JSONArray elements, @NonNull @lombok.NonNull final Item item) throws JSONException {
        for (int i = 0; i < elements.length(); i++) {
            final JSONObject json = elements.getJSONObject(i);
            if ("div".equals(json.getString(ScmpParser.JSON_TYPE))) {
                if (!json.has(ScmpParser.JSON_CHILDREN)) continue;

                final JSONArray array = json.getJSONArray(ScmpParser.JSON_CHILDREN);
                for (int j = 0; j < array.length(); j++) {
                    String videoId = array.getJSONObject(j).optString("video_id");
                    if (videoId.isEmpty()) {
                        final JSONObject attribs = array.getJSONObject(j).optJSONObject(ScmpParser.JSON_ATTRIBS);
                        if (attribs != null) {
                            final String src = attribs.optString("src");
                            if (!src.isEmpty()) videoId = StringUtils.substringAfter(src, "?id=");
                        }
                    }

                    if (!videoId.isEmpty()) item.getVideos().add(new Video("https://cf.cdn.vid.ly/" + videoId + "/hd_mp4.mp4", "https://vid.ly/" + videoId + "/poster_hd"));
                }
            }
        }
    }
}
