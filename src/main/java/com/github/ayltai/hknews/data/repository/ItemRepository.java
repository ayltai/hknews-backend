package com.github.ayltai.hknews.data.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Item;

import io.micrometer.core.annotation.Timed;

public interface ItemRepository extends MongoRepository<Item, Integer> {
    @NonNull
    @Timed("repo_item_find_by_source_in_and_category_name_in_and_publish_date_after")
    List<Item> findBySourceInAndCategoryNameInAndPublishDateAfter(@NonNull @lombok.NonNull Collection<String> sourceNames, @NonNull @lombok.NonNull Collection<String> categoryNames, @NonNull @lombok.NonNull Date publishDate, @NonNull @lombok.NonNull Sort sort);

    @Timed("repo_item_delete_by_publish_date_before")
    long deleteByPublishDateBefore(@NonNull @lombok.NonNull Date publishDate);
}
