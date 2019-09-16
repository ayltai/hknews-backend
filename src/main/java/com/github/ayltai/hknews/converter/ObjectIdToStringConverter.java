package com.github.ayltai.hknews.converter;

import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public final class ObjectIdToStringConverter implements Converter<ObjectId, String> {
    @NonNull
    @Override
    public String convert(@NonNull @lombok.NonNull final ObjectId source) {
        return source.toHexString();
    }
}
