package com.github.ayltai.hknews.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.ItemRepository;

@RestController
@RequestMapping(
    path   = "/items",
    method = {
        RequestMethod.GET,
        RequestMethod.HEAD,
        RequestMethod.OPTIONS
    }
)
@CrossOrigin
public class ItemController {
    private final ItemRepository itemRepository;

    public ItemController(@NonNull @lombok.NonNull final ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @NonNull
    @Cacheable(
        cacheNames = "items",
        sync       = true
    )
    @GetMapping(
        path     = "/{sourceNames}/{categoryNames}/{days}",
        produces = "application/json"
    )
    public Iterable<Item> getItems(
        @NonNull @lombok.NonNull @PathVariable final List<String> sourceNames,
        @NonNull @lombok.NonNull @PathVariable final List<String> categoryNames,
        @PathVariable final int days,
        @PageableDefault(
            page = 0,
            size = 2000
        )
        @SortDefault.SortDefaults({
            @SortDefault(
                sort      = Item.FIELD_PUBLISH_DATE,
                direction = Sort.Direction.DESC
            )
        })
        final Pageable pageable) {
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
