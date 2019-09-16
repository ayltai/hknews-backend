package com.github.ayltai.hknews.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.ItemRepository;

@Service
public final class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(@NonNull @lombok.NonNull final ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Nullable
    public Item getItem(@NonNull @lombok.NonNull final ObjectId id) {
        return this.itemRepository.findBy_id(id);
    }

    @NonNull
    public Page<Item> getItems(@NonNull @lombok.NonNull final List<String> sourceNames, @NonNull @lombok.NonNull final List<String> categoryNames, final int days, @Nullable final String keywords, final Pageable pageable) {
        final List<String> names = new ArrayList<>();
        for (final String sourceName : sourceNames) names.addAll(Source.fromDisplayName(sourceName));

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -days);

        return keywords == null
            ? this.itemRepository.findBySourceInAndCategoryNameInAndPublishDateAfter(names, categoryNames, calendar.getTime(), pageable)
            : this.itemRepository.findBySourceInAndCategoryNameInAndPublishDateAfter(names, categoryNames, calendar.getTime(), keywords, pageable);
    }
}
