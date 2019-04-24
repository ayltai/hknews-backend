package com.github.ayltai.hknews.rss;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name = "rss", strict = false)
public final class Feed {
    @Element(name = "title", required = false)
    @Path("channel")
    private String title;

    @Element(name = "description", required = false)
    @Path("channel")
    private String description;

    @Element(name = "copyright", required = false)
    @Path("channel")
    private String copyright;

    @ElementList(name = "item", inline = true, required = false)
    @Path("channel")
    private List<Item> items;

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCopyright() {
        return this.copyright;
    }

    public List<Item> getItems() {
        return this.items;
    }
}
