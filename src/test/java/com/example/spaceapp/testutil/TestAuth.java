package com.example.spaceapp.testutil;

import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public final class TestAuth {
    private TestAuth() {}

    public static String basicAuthHeaderValue(String username, String password) {
        String token = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    public static void applyBasicAuth(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder builder,
                                      String username,
                                      String password) {
        builder.header(HttpHeaders.AUTHORIZATION, basicAuthHeaderValue(username, password));
    }
}
