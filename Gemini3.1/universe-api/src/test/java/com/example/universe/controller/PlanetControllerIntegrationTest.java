package com.example.universe.controller;

import com.example.universe.dto.PlanetDTO;
import com.example.universe.entity.Planet;
import com.example.universe.repository.PlanetRepository;
import com.example.universe.testutil.JsonUtil;
import com.example.universe.testutil.TestUsers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PlanetControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanetRepository planetRepository;

    private Long existingPlanetId;

    private String basic(String u, String p) {
        String token = u + ":" + p;
        return "Basic " + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void setup() {
        Planet p = planetRepository.save(Planet.builder()
                .name("Mercury")
                .type("Terrestrial")
                .radiusKm(2439.7)
                .massKg(3.301e23)
                .orbitalPeriodDays(88.0)
                .build());
        planetRepository.flush();
        existingPlanetId = p.getId();
    }

    @Test
    void unauthenticatedRequests_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/planets"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void student_canReadButCannotWrite() throws Exception {
        mockMvc.perform(get("/api/planets")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/planets/" + existingPlanetId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mercury"));

        PlanetDTO dto = PlanetDTO.builder()
                .name("Pluto")
                .type("Dwarf")
                .radiusKm(1188.3)
                .massKg(1.309e22)
                .orbitalPeriodDays(90560.0)
                .build();

        mockMvc.perform(post("/api/planets")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void staff_canCreateUpdateDelete_andSearchByType() throws Exception {
        PlanetDTO create = PlanetDTO.builder()
                .name("Venus")
                .type("Terrestrial")
                .radiusKm(6051.8)
                .massKg(4.867e24)
                .orbitalPeriodDays(224.7)
                .build();

        String response = mockMvc.perform(post("/api/planets")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Venus"))
                .andReturn().getResponse().getContentAsString();

        long createdId = Long.parseLong(response.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1"));

        PlanetDTO update = PlanetDTO.builder()
                .name("Venus")
                .type("Terrestrial")
                .radiusKm(6052.0)
                .massKg(4.867e24)
                .orbitalPeriodDays(224.7)
                .build();

        mockMvc.perform(put("/api/planets/" + createdId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.radiusKm").value(6052.0));

        mockMvc.perform(get("/api/planets")
                        .queryParam("type", "terrestrial")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/planets/" + createdId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS)))
                .andExpect(status().isNoContent());
    }
}
