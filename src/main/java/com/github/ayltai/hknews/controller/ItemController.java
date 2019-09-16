package com.github.ayltai.hknews.controller;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.service.ItemService;

@RestController
@RequestMapping(method = {
    RequestMethod.GET,
    RequestMethod.HEAD,
    RequestMethod.OPTIONS
})
@CrossOrigin
public class ItemController {
    private final ItemService itemService;

    public ItemController(@NonNull @lombok.NonNull final ItemService itemService) {
        this.itemService = itemService;
    }

    @NonNull
    @GetMapping(
        path     = "/item/{id}",
        produces = "application/json"
    )
    public ResponseEntity<Item> getItem(@PathVariable @Nullable final String id) {
        if (id == null) return ResponseEntity.badRequest().build();

        final Item item = this.itemService.getItem(new ObjectId(id));
        if (item == null) return ResponseEntity.notFound().build();

        item.setId(item.get_id().toHexString());

        return ResponseEntity.ok(item);
    }

    @NonNull
    @Cacheable(
        cacheNames = "items",
        sync       = true
    )
    @GetMapping(
        path     = "/items/{sourceNames}/{categoryNames}/{days}",
        produces = "application/json"
    )
    public ResponseEntity<Page<Item>> getItems(
        @PathVariable @Nullable final List<String> sourceNames,
        @PathVariable @Nullable final List<String> categoryNames,
        @PathVariable final int days,
        @RequestParam @Nullable final String keywords,
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
        if (sourceNames == null || sourceNames.isEmpty() || categoryNames == null || categoryNames.isEmpty()) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(this.itemService
            .getItems(sourceNames, categoryNames, days, keywords, pageable)
            .map(item -> {
                item.setId(item.get_id().toHexString());

                return item;
            }));
    }
}
