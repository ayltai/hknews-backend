package com.github.ayltai.hknews.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.service.SourceService;

@RestController
@RequestMapping(
    path   = "/sources",
    method = {
        RequestMethod.GET,
        RequestMethod.HEAD,
        RequestMethod.OPTIONS
    }
)
public class SourceController {
    private final SourceService sourceService;

    public SourceController(@NonNull @lombok.NonNull final SourceService sourceService) {
        this.sourceService = sourceService;
    }

    @NonNull
    @Cacheable(
        cacheNames = "sources",
        sync       = true
    )
    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<Source>> getSources(
        @PageableDefault(
            page = 0,
            size = 20
        )
        final Pageable pageable) {
        return ResponseEntity.ok(this.sourceService.getAllSources(pageable));
    }
}
