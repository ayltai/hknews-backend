package com.github.ayltai.hknews.net;

import org.springframework.lang.NonNull;

import com.github.ayltai.hknews.rss.Feed;

import io.micrometer.core.annotation.Timed;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface ApiService {
    @NonNull
    @Timed("ext_get_feed")
    @GET
    Call<Feed> getFeed(@Url String url);

    @NonNull
    @Timed("ext_get_html")
    @GET
    Call<String> getHtml(@Url String url);

    @NonNull
    @Timed("ext_post_html")
    @FormUrlEncoded
    @POST
    Call<String> postHtml(@Url String url, @Field("sid") int sectionId, @Field("p") int page);
}
