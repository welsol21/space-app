package com.example.universe.controller;

import com.example.universe.dto.MoonDTO;
import com.example.universe.entity.Planet;
import com.example.universe.repository.MoonRepository;
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
class MoonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private MoonRepository moonRepository;

    private Long planetId;

    private String basic(String u, String p) {
        String token = u + ":" + p;
        return "Basic " + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void setup() {
        Planet earth = planetRepository.save(Planet.builder()
                .name("Earth").type("Terrestrial").radiusKm(6371.0).massKg(5.972e24).orbitalPeriodDays(365.25)
                .build());
        planetRepository.flush();
        planetId = earth.getId();
    }

    @Test
    void unauthenticatedRequests_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/moons"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void student_canReadButCannotWrite() throws Exception {
        mockMvc.perform(get("/api/moons")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS)))
                .andExpect(status().isOk());

        MoonDTO dto = MoonDTO.builder()
                .name("Luna").diameterKm(3474.8).orbitalPeriodDays(27.3)
                .planetId(planetId)
                .build();

        mockMvc.perform(post("/api/moons")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void staff_canCreateUpdateDelete_andQueries() throws Exception {
        MoonDTO dto = MoonDTO.builder()
                .name("Luna").diameterKm(3474.8).orbitalPeriodDays(27.3)
                .planetId(planetId)
                .build();

        String response = mockMvc.perform(post("/api/moons")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Luna"))
                .andReturn().getResponse().getContentAsString();

        long moonId = Long.parseLong(response.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1"));

        MoonDTO update = MoonDTO.builder()
                .name("Luna").diameterKm(3475.0).orbitalPeriodDays(27.3)
                .planetId(planetId)
                .build();

        mockMvc.perform(put("/api/moons/" + moonId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diameterKm").value(3475.0));

        mockMvc.perform(get("/api/moons")
                        .queryParam("planetName", "Earth")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists());

        mockMvc.perform(get("/api/moons/count")
                        .queryParam("planetId", String.valueOf(planetId))
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.matchesRegex("\\d+")));

        mockMvc.perform(delete("/api/moons/" + moonId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS)))
                .andExpect(status().isNoContent());
    }
}
