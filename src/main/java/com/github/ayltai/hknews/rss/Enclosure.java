package com.github.ayltai.hknews.rss;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;
import org.springframework.lang.NonNull;

@Root(name = "enclosure", strict = false)
public final class Enclosure {
    private static final String TYPE_IMAGE = "image/";
    private static final String TYPE_VIDEO = "video/";

    @Attribute(name = "url")
    private String url;

    @Attribute(name = "length", required = false)
    private long length;

    @Attribute(name = "type", required = false)
    private String type;

    @NonNull
    public String getUrl() {
        return this.url;
    }

    public void setUrl(@NonNull @lombok.NonNull final String url) {
        this.url = url;
    }

    public long getLength() {
        return this.length;
    }

    public String getType() {
        return this.type;
    }

    public boolean isImage() {
        return this.type.startsWith(Enclosure.TYPE_IMAGE);
    }

    public boolean isVideo() {
        return this.type.startsWith(Enclosure.TYPE_VIDEO);
    }
}
