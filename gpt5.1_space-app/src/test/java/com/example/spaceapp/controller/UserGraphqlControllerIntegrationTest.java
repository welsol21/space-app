package com.example.spaceapp.controller;

import com.example.spaceapp.testutil.TestUsers;
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
    void graphql_userById_and_createUser_security() throws Exception {
        // student should be forbidden for GraphQL per security config
        String query = "{\"query\":\"query { userById(id: 1) { id username role } }\"}";
        mockMvc.perform(post("/graphql")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(query))
                .andExpect(status().isForbidden());

        // admin can create user via mutation
        String mutation = "{\"query\":\"mutation { createUser(input: { username: \\\"u1\\\", password: \\\"p\\\", role: ADMIN }) { id username role } }\"}";
        mockMvc.perform(post("/graphql")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mutation))
                .andExpect(status().isOk());
    }
}
