package com.github.ayltai.hknews.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

public class BaseController {
    protected <T> ResponseEntity<T> createResponse(@NonNull @lombok.NonNull final T body) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add("X-Content-Type-Options", "nosniff");
        headers.add("X-Frame-Options", "DENY");
        headers.add("X-XSS-Protection", "1; mode=block");

        return new ResponseEntity<>(body, headers, HttpStatus.OK);
    }
}
