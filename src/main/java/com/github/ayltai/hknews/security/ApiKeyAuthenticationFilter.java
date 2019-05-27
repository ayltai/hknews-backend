package com.github.ayltai.hknews.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.github.ayltai.hknews.analytics.AnalyticsFactory;

final class ApiKeyAuthenticationFilter extends BasicAuthenticationFilter {
    ApiKeyAuthenticationFilter(@NonNull @lombok.NonNull final AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(@NonNull @lombok.NonNull final HttpServletRequest request, @NonNull @lombok.NonNull final HttpServletResponse response, @NonNull @lombok.NonNull final FilterChain chain) throws IOException, ServletException {
        final long startTime = System.currentTimeMillis();

        final String apiKey = request.getParameter("apiKey");

        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext()
            .setAuthentication(this.getAuthenticationManager()
                .authenticate(new ApiKeyAuthenticationToken(apiKey == null ? request.getHeader("x-api-key") : apiKey)));

        AnalyticsFactory.create(request.getRemoteAddr())
            .timing()
            .userTimingCategory("Performance")
            .userTimingLabel("Authentication Filter")
            .userTimingTime(Long.valueOf(System.currentTimeMillis() - startTime).intValue())
            .send();

        chain.doFilter(request, response);
    }
}
