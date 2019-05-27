package com.github.ayltai.hknews.data.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Document
public final class Source implements Comparable<Source> {
    //region Constants

    private static final String SING_TAO_DAILY    = "星島日報";
    private static final String SING_TAO_REALTIME = "星島即時";
    private static final String HEADLINE_DAILY    = "頭條日報";
    private static final String HEADLINE_REALTIME = "頭條即時";

    //endregion

    @Id
    @EqualsAndHashCode.Include
    @Getter
    private String name;

    @Getter
    private String imageUrl;

    @NonNull
    @Getter
    private List<Category> categories = new ArrayList<>();

    @Override
    public int compareTo(@NonNull final Source source) {
        return this.name.compareTo(source.name);
    }

    @NonNull
    public static String toDisplayName(@NonNull final String name) {
        if (Source.SING_TAO_REALTIME.equals(name)) return Source.SING_TAO_DAILY;
        if (Source.HEADLINE_REALTIME.equals(name)) return Source.HEADLINE_DAILY;

        return name;
    }

    @NonNull
    public static Collection<String> fromDisplayName(@NonNull final String name) {
        if (name.equals(Source.SING_TAO_DAILY)) return Arrays.asList(name, Source.SING_TAO_REALTIME);
        if (name.equals(Source.HEADLINE_DAILY)) return Arrays.asList(name, Source.HEADLINE_REALTIME);

        return Collections.singletonList(name);
    }
}
