package com.github.ayltai.hknews.converter;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public final class StringToObjectIdConverter implements Converter<String, ObjectId> {
    @NonNull
    @Override
    public ObjectId convert(@NonNull @lombok.NonNull final String source) {
        return new ObjectId(source);
    }
}
