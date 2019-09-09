package com.github.ayltai.hknews.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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
    public Item getItem(@NonNull @lombok.NonNull final String url) {
        final Item item = new Item();
        item.setUrl(url);

        return this.itemRepository.findOne(Example.of(item)).orElse(null);
    }

    @NonNull
    public Page<Item> getItems(@NonNull @lombok.NonNull @PathVariable final List<String> sourceNames, @NonNull @lombok.NonNull @PathVariable final List<String> categoryNames, @PathVariable final int days, final Pageable pageable) {
        final List<String> names = new ArrayList<>();
        for (final String sourceName : sourceNames) names.addAll(Source.fromDisplayName(sourceName));

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -days);

        return this.itemRepository.findBySourceInAndCategoryNameInAndPublishDateAfter(names, categoryNames, calendar.getTime(), pageable);
    }
}
