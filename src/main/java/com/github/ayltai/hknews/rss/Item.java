package com.github.ayltai.hknews.rss;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.springframework.lang.NonNull;

@Root(name = "item", strict = false)
public final class Item {
    @Path("title")
    @Text(data = true)
    private String title;

    @Path("link")
    @Text(required = false)
    private String link;

    @Path("guid")
    @Text(required = false)
    private String guid;

    @Path("description")
    @Text(required = false, data = true)
    private String description;

    @Path("pubDate")
    @Text(required = false)
    private String pubDate;

    @ElementList(name = "enclosure", required = false, type = Enclosure.class, inline = true)
    private List<Enclosure> enclosures;

    @NonNull
    public String getTitle() {
        return this.title.replace("\uFEFF", "");
    }

    public void setTitle(@NonNull @lombok.NonNull final String title) {
        this.title = title;
    }

    public String getLink() {
        return this.link == null ? this.guid : this.link;
    }

    public String getDescription() {
        return this.description;
    }

    public String getPubDate() {
        return this.pubDate;
    }

    public List<Enclosure> getEnclosures() {
        return this.enclosures;
    }
}
