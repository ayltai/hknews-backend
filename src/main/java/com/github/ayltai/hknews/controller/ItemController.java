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
import org.springframework.web.bind.annotation.RestController;

import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.diagnostic.AgentFactory;
import com.github.ayltai.hknews.service.ItemService;
import com.instrumentalapp.Agent;

@RestController
@RequestMapping(method = {
    RequestMethod.GET,
    RequestMethod.HEAD,
    RequestMethod.OPTIONS
})
@CrossOrigin
public class ItemController {
    private static final String METRIC_REQUEST       = "app.api.request";
    private static final String METRIC_REQUEST_ITEM  = "app.api.request.item";
    private static final String METRIC_REQUEST_ITEMS = "app.api.request.items";

    private final ItemService  itemService;
    private final AgentFactory agentFactory;

    private Agent agent;

    public ItemController(@NonNull @lombok.NonNull final ItemService itemService, @NonNull @lombok.NonNull final AgentFactory agentFactory) {
        this.itemService  = itemService;
        this.agentFactory = agentFactory;
    }

    @NonNull
    @GetMapping(
        path     = "/item/{id}",
        produces = "application/json"
    )
    public ResponseEntity<Item> getItem(@PathVariable @Nullable final String id) {
        final long startTime = System.currentTimeMillis();

        if (id == null) return ResponseEntity.badRequest().build();

        final Item item = this.itemService.getItem(new ObjectId(id));
        if (item == null) return ResponseEntity.notFound().build();

        item.setRecordId(item.get_id().toHexString());

        if (this.agent == null) this.agent = this.agentFactory.create();
        if (this.agent != null) {
            this.agent.gauge(ItemController.METRIC_REQUEST_ITEM, System.currentTimeMillis() - startTime);
            this.agent.gauge(ItemController.METRIC_REQUEST, System.currentTimeMillis() - startTime);
        }

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
        final long startTime = System.currentTimeMillis();

        if (sourceNames == null || sourceNames.isEmpty() || categoryNames == null || categoryNames.isEmpty()) return ResponseEntity.badRequest().build();

        final Page<Item> items = this.itemService
            .getItems(sourceNames, categoryNames, days, pageable)
            .map(item -> {
                item.setRecordId(item.get_id().toHexString());

                return item;
            });

        if (this.agent == null) this.agent = this.agentFactory.create();
        if (this.agent != null) {
            this.agent.gauge(ItemController.METRIC_REQUEST_ITEMS, System.currentTimeMillis() - startTime);
            this.agent.gauge(ItemController.METRIC_REQUEST, System.currentTimeMillis() - startTime);
        }

        return ResponseEntity.ok(items);
    }
}
