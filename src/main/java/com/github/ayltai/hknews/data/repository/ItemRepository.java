package com.github.ayltai.hknews.data.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.github.ayltai.hknews.data.model.Item;

public interface ItemRepository extends MongoRepository<Item, Integer> {
    @NonNull
    Page<Item> findBySourceInAndCategoryNameInAndPublishDateAfter(@NonNull @lombok.NonNull Collection<String> sourceNames, @NonNull @lombok.NonNull Collection<String> categoryNames, @NonNull @lombok.NonNull Date publishDate, Pageable pageable);

    @Nullable
    Item findByUrl(@NonNull @lombok.NonNull String url);

    long deleteByPublishDateBefore(@NonNull @lombok.NonNull Date publishDate);
}
