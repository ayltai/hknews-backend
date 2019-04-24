package com.github.ayltai.hknews.data.model;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.lang.NonNull;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
public final class Category {
    private static final String REALTIME = "即時";

    @EqualsAndHashCode.Include
    @Getter
    private String url;

    @Getter
    private String name;

    @NonNull
    public static String toDisplayName(@NonNull final String name) {
        return name.startsWith(Category.REALTIME) ? name.substring(2) : name;
    }

    @NonNull
    public static Collection<String> fromDisplayName(@NonNull final String name) {
        return Arrays.asList(name, Category.REALTIME + name);
    }
}
