package com.github.ayltai.hknews.data.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.data.model.Item;

public interface ItemRepository extends MongoRepository<Item, Integer> {
    @NonNull
    List<Item> findBySourceInAndCategoryNameInAndPublishDateAfter(@NonNull @lombok.NonNull Collection<String> sourceNames, @NonNull @lombok.NonNull Collection<String> categoryNames, @NonNull @lombok.NonNull Date publishDate, @NonNull @lombok.NonNull Sort sort);

    long deleteByPublishDateBefore(@NonNull @lombok.NonNull Date publishDate);
}
