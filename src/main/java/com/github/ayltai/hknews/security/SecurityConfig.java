package com.github.ayltai.hknews.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(@NonNull @lombok.NonNull final AuthenticationManagerBuilder auth) throws Exception {
        final String apiKey = System.getenv("api_key");

        auth.inMemoryAuthentication()
            .withUser(apiKey)
            .password(apiKey)
            .roles("USER");
    }

    @Override
    protected void configure(@NonNull @lombok.NonNull final HttpSecurity http) throws Exception {
        http.requiresChannel()
            .anyRequest()
            .requiresSecure()
            .and()
            .addFilterAt(new ApiKeyAuthenticationFilter(new ApiKeyAuthenticationManager()), BasicAuthenticationFilter.class)
            .authorizeRequests()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic();
    }

    @Override
    public void configure(@NonNull @lombok.NonNull final WebSecurity web) throws Exception {
        web.ignoring()
            .antMatchers("/images/**");
    }
}
