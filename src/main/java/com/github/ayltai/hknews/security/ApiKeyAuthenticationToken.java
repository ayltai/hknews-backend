package com.github.ayltai.hknews.security;

import java.util.Collections;

import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

final class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {
    private final String apiKey;

    ApiKeyAuthenticationToken(@Nullable final String apiKey) {
        super(Collections.emptyList());

        this.apiKey = apiKey;
    }

    @Nullable
    @Override
    public Object getCredentials() {
        return this.apiKey;
    }

    @Nullable
    @Override
    public Object getPrincipal() {
        return this.apiKey;
    }
}
