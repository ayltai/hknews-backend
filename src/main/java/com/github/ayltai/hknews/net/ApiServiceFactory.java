package com.github.ayltai.hknews.net;

import org.springframework.lang.NonNull;

public interface ApiServiceFactory {
    @NonNull
    ApiService create();
}
