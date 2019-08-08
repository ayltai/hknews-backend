package com.github.ayltai.hknews.data.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.github.ayltai.hknews.data.model.Source;

import io.micrometer.core.annotation.Timed;

public interface SourceRepository extends MongoRepository<Source, Integer> {
    @Nullable
    @Timed("repo_source_find_by_name")
    Source findByName(@NonNull @lombok.NonNull String name);
}
