package com.github.ayltai.hknews.security;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

final class ApiKeyAuthenticationFilter extends BasicAuthenticationFilter {
    ApiKeyAuthenticationFilter(@NonNull @lombok.NonNull final AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(@NonNull @lombok.NonNull final HttpServletRequest request, @NonNull @lombok.NonNull final HttpServletResponse response, @NonNull @lombok.NonNull final FilterChain chain) throws IOException, ServletException {
        final String ip = request.getRemoteAddr();

        SecurityContextHolder.clearContext();
        SecurityContextHolder.getContext()
            .setAuthentication(this.getAuthenticationManager()
                .authenticate(new ApiKeyAuthenticationToken(ip.equals(System.getenv("trusted_ip")) || ip.equals("127.0.0.1") || ip.equals("localhost") ? System.getenv("api_key") : request.getHeader("x-api-key"))));

        chain.doFilter(request, response);
    }
}
