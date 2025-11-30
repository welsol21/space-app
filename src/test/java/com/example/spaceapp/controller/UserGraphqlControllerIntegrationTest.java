package com.example.spaceapp.controller;

import com.example.spaceapp.model.User;
import com.example.spaceapp.model.UserRole;
import com.example.spaceapp.repository.UserRepository;
import com.example.spaceapp.testutil.TestUsers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserGraphqlControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String basic(String u, String p) {
        String token = u + ":" + p;
        return "Basic " + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void graphql_requiresAdminRole() throws Exception {
        String body = "{\"query\":\"{ userById(id: 1) { id username role } }\"}";

        mockMvc.perform(post("/graphql")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/graphql")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/graphql")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isForbidden());
    }

    @Test
    void admin_canQueryUserById() throws Exception {
        User admin = userRepository.findByUsername(TestUsers.ADMIN_USER).orElseThrow();

        String body = "{\"query\":\"query($id: ID!){ userById(id: $id){ id username role } }\",\"variables\":{\"id\":" + admin.getId() + "}}";

        mockMvc.perform(post("/graphql")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userById.username").value(TestUsers.ADMIN_USER))
                .andExpect(jsonPath("$.data.userById.role").value("ADMIN"));
    }

    @Test
    void admin_canCreateUserViaMutation_andPasswordIsEncodedInDb() throws Exception {
        String body = "{\"query\":\"mutation($input: CreateUserInput!) { createUser(input: $input) { id username role } }\"," +
                "\"variables\":{\"input\":{\"username\":\"graphqlUser\",\"password\":\"graphql123\",\"role\":\"STAFF\"}}}";

        mockMvc.perform(post("/graphql")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.createUser.username").value("graphqlUser"))
                .andExpect(jsonPath("$.data.createUser.role").value("STAFF"))
                .andExpect(jsonPath("$.data.createUser.id").isNotEmpty());

        User created = userRepository.findByUsername("graphqlUser").orElseThrow();
        assertThat(created.getRole()).isEqualTo(UserRole.STAFF);
        assertThat(passwordEncoder.matches("graphql123", created.getPassword())).isTrue();
    }
}
