package com.github.ayltai.hknews;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.server.Ssl;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public final class SecretConfig {
    @NonNull
    @Bean
    public ServerProperties getServerProperties() {
        final String key = System.getenv("ssl_key");
        final Ssl    ssl = new Ssl();

        ssl.setKeyPassword(key);
        ssl.setKeyStorePassword(key);
        ssl.setTrustStorePassword(key);

        System.setProperty("server.ssl.key-store-password", key);
        System.setProperty("server.ssl.trust-store-password", key);

        final ServerProperties properties = new ServerProperties();
        properties.setSsl(ssl);

        return properties;
    }
}
