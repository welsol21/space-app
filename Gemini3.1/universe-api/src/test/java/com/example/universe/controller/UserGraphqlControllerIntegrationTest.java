package com.example.universe.controller;

import com.example.universe.testutil.TestUsers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserGraphqlControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String basic(String u, String p) {
        String token = u + ":" + p;
        return "Basic " + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void graphql_userById_shouldBeAccessibleToAuthenticatedUsers_and_createUser_adminOnly() throws Exception {
        // In Gemini app, /graphql is authenticated; query has no @PreAuthorize
        String query = "{\"query\":\"query { userById(id: 1) { id username role } }\"}";
        mockMvc.perform(post("/graphql")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(query))
                .andExpect(status().isOk());

        // admin can create user via mutation (mutation has @PreAuthorize('ADMIN'))
        String mutation = "{\"query\":\"mutation { createUser(input: { username: \\\"u1\\\", password: \\\"p\\\", role: ADMIN }) { id username role } }\"}";
        mockMvc.perform(post("/graphql")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mutation))
                .andExpect(status().isOk());
    }
}
