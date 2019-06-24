package com.github.ayltai.hknews.net;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.github.ayltai.hknews.AppConfig;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@Component
public final class DefaultApiServiceFactory implements ApiServiceFactory {
    private final AppConfig config;

    @Autowired
    private DefaultApiServiceFactory(@NonNull @lombok.NonNull final AppConfig config) {
        this.config = config;
    }

    @NonNull
    public ApiService create() {
        return new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://github.com")
            .client(new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(this.config.getConnectionPoolSize(), this.config.getIdleTimeout(), TimeUnit.SECONDS))
                .connectTimeout(this.config.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(this.config.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(this.config.getWriteTimeout(), TimeUnit.SECONDS)
                .addInterceptor(chain -> chain.proceed(chain.request()
                    .newBuilder()
                    .header("User-Agent", this.config.getUserAgent())
                    .build()))
                .addInterceptor(chain -> {
                    final Response response = chain.proceed(chain.request());

                    if (chain.request().url().host().contains("news.wenweipo.com")) {
                        final ResponseBody body = response.body();

                        if (body == null) return response;

                        return response.newBuilder()
                            .body(ResponseBody.create(body.contentType(), new String(body.bytes(), "Big5")))
                            .build();
                    }

                    return response;
                })
                .build())
            .build()
            .create(ApiService.class);
    }
}
