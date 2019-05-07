package com.github.ayltai.hknews.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.ItemRepository;

@RestController
@RequestMapping("/items")
public final class ItemController {
    private final ItemRepository itemRepository;

    public ItemController(@NonNull @lombok.NonNull final ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @NonNull
    @GetMapping(
        path     = "/{sourceNames}/{categoryNames}/{days}",
        produces = "application/json"
    )
    public Iterable<Item> getItems(@NonNull @lombok.NonNull @PathVariable final List<String> sourceNames, @NonNull @lombok.NonNull @PathVariable final List<String> categoryNames, @PathVariable final int days) {
        final List<String> names = new ArrayList<>();
        for (final String sourceName : sourceNames) names.addAll(Source.fromDisplayName(sourceName));

        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, -days);

        return this.itemRepository.findBySourceInAndCategoryNameInAndPublishDateAfter(names, categoryNames, calendar.getTime(), Sort.by(Sort.Direction.DESC, Item.FIELD_PUBLISH_DATE));
    }
}
