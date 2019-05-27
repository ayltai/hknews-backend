package com.github.ayltai.hknews.analytics;

import org.springframework.lang.NonNull;

import com.brsanthu.googleanalytics.GoogleAnalytics;
import com.brsanthu.googleanalytics.request.DefaultRequest;

import lombok.experimental.UtilityClass;

@UtilityClass
public class AnalyticsFactory {
    public GoogleAnalytics create(@NonNull @lombok.NonNull final String remoteAddress) {
        return GoogleAnalytics.builder()
            .withDefaultRequest(new DefaultRequest()
                .trackingId(System.getenv("ga_tracking_id"))
                .userIp(remoteAddress))
            .build();
    }
}
