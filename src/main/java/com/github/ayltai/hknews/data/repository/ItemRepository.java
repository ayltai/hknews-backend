package com.github.ayltai.hknews.data.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Item;

public interface ItemRepository extends MongoRepository<Item, Integer> {
    @NonNull
    List<Item> findBySourceInAndCategoryNameInAndPublishDateAfter(
        @NonNull @lombok.NonNull Collection<String> sourceNames,
        @NonNull @lombok.NonNull Collection<String> categoryNames,
        @NonNull @lombok.NonNull Date publishDate,
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
        Pageable pageable);

    long deleteByPublishDateBefore(@NonNull @lombok.NonNull Date publishDate);
}
