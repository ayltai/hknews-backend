package com.github.ayltai.hknews.parser;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;

import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Parser {
    @NonNull
    @lombok.NonNull
    protected final ApiServiceFactory apiServiceFactory;

    @NonNull
    @lombok.NonNull
    protected final SourceRepository sourceRepository;

    @NonNull
    @lombok.NonNull
    protected final ItemRepository itemRepository;

    @NonNull
    public abstract Source getSource();

    @NonNull
    public abstract Collection<Item> getItems(@NonNull @lombok.NonNull Category category) throws IOException;

    @NonNull
    public abstract Item getItem(@NonNull @lombok.NonNull Item item) throws IOException;

    protected static Date toSafeDate(@NonNull @lombok.NonNull final Date date) {
        final Date now = new Date();

        return date.after(now) ? now : date;
    }
}
