package com.example.spaceapp.controller;

import com.example.spaceapp.dto.PlanetCreateUpdateDto;
import com.example.spaceapp.model.Planet;
import com.example.spaceapp.repository.PlanetRepository;
import com.example.spaceapp.testutil.JsonUtil;
import com.example.spaceapp.testutil.TestUsers;
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
        Long id = planetRepository.findByNameIgnoreCase("Earth").map(com.example.spaceapp.model.Planet::getId).orElseThrow();
        existingPlanetId = id;
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
                .andExpect(jsonPath("$.name").value("Earth"));

        PlanetCreateUpdateDto dto = new PlanetCreateUpdateDto();
        dto.setName("Pluto");
        dto.setType("Dwarf");
        dto.setRadiusKm(1188.3);
        dto.setMassKg(1.309e22);
        dto.setOrbitalPeriodDays(90560.0);

        mockMvc.perform(post("/api/planets")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void staff_canCreateUpdateDelete() throws Exception {
        PlanetCreateUpdateDto create = new PlanetCreateUpdateDto();
                create.setName("Venus");
                create.setType("Terrestrial");
                create.setRadiusKm(6051.8);
                create.setMassKg(4.867e24);
                create.setOrbitalPeriodDays(224.7);

        String response = mockMvc.perform(post("/api/planets")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Venus"))
                .andReturn().getResponse().getContentAsString();

        long createdId = Long.parseLong(response.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1"));

        PlanetCreateUpdateDto update = new PlanetCreateUpdateDto();
                update.setName("Venus");
                update.setType("Terrestrial");
                update.setRadiusKm(6052.0);
                update.setMassKg(4.867e24);
                update.setOrbitalPeriodDays(224.7);

        mockMvc.perform(put("/api/planets/" + createdId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.radiusKm").value(6052.0));

        mockMvc.perform(delete("/api/planets/" + createdId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS)))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchAndNamesEndpoints_shouldWork_orFailIfMissing() throws Exception {
        mockMvc.perform(get("/api/planets/search/by-type")
                        .queryParam("type", "terrestrial")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/planets/names")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk());
    }

    @Test
    void nameAndMassEndpoint_shouldReturnExpectedFields_orFailIfMissing() throws Exception {
        mockMvc.perform(get("/api/planets/fields/name-mass")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk());
    }
}
