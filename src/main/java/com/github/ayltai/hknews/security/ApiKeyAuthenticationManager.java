package com.github.ayltai.hknews.security;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

final class ApiKeyAuthenticationManager implements AuthenticationManager {
    @Override
    public Authentication authenticate(@NonNull @lombok.NonNull final Authentication authentication) throws AuthenticationException {
        final Object username = authentication.getPrincipal();
        final Object password = authentication.getCredentials();

        if (username == null || password == null) return authentication;

        final String apiKey = System.getenv("api_key");
        if (username.equals(apiKey) && password.equals(apiKey)) authentication.setAuthenticated(true);

        return authentication;
    }
}
