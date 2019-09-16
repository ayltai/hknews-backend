package com.github.ayltai.hknews.data.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.github.ayltai.hknews.data.model.Item;

public interface ItemRepository extends MongoRepository<Item, Integer> {
    @NonNull
    Page<Item> findBySourceInAndCategoryNameInAndPublishDateAfter(@NonNull @lombok.NonNull Collection<String> sourceNames, @NonNull @lombok.NonNull Collection<String> categoryNames, @NonNull @lombok.NonNull Date publishDate, Pageable pageable);

    @Query("{ $and : [ { \"sources.name\" : { $in : ?0 } }, { \"categories.name\" : { $in : ?1 } }, { publishDate : { $gt : ?2 } }, { $or : [ { title : { $regex : '(?i)?3' } }, { description : { $regex : '(?i)?3' } } ] } ] }")
    @NonNull
    Page<Item> findBySourceInAndCategoryNameInAndPublishDateAfter(@NonNull @lombok.NonNull Collection<String> sourceNames, @NonNull @lombok.NonNull Collection<String> categoryNames, @NonNull @lombok.NonNull Date publishDate, @NonNull @lombok.NonNull String keywords, Pageable pageable);

    @Nullable
    Item findBy_id(@NonNull @lombok.NonNull ObjectId _id);

    long deleteByPublishDateBefore(@NonNull @lombok.NonNull Date publishDate);
}
