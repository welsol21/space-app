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
        Planet p = planetRepository.save(Planet.builder()
                .name("TestMercury")
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
                .andExpect(jsonPath("$.name").value("TestMercury"));

        PlanetCreateUpdateDto dto = PlanetCreateUpdateDto.builder()
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
    void staff_canCreateUpdateDelete() throws Exception {
        PlanetCreateUpdateDto create = PlanetCreateUpdateDto.builder()
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

        // naive extraction to avoid adding custom libs
        long createdId = Long.parseLong(response.replaceAll(".*\"id\"\s*:\s*(\\d+).*", "$1"));

        PlanetCreateUpdateDto update = PlanetCreateUpdateDto.builder()
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

        mockMvc.perform(delete("/api/planets/" + createdId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS)))
                .andExpect(status().isNoContent());
    }

    @Test
    void createPlanet_duplicateName_shouldReturn400_ErrorResponse() throws Exception {
        PlanetCreateUpdateDto dto = PlanetCreateUpdateDto.builder()
                .name("TestMercury") // already exists
                .type("Terrestrial")
                .radiusKm(2439.7)
                .massKg(3.301e23)
                .orbitalPeriodDays(88.0)
                .build();

        mockMvc.perform(post("/api/planets")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/planets"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void getPlanetById_missing_shouldReturn404_ErrorResponse() throws Exception {
        mockMvc.perform(get("/api/planets/999999")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/planets/999999"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void validationError_shouldReturn400_ErrorResponse() throws Exception {
        // name too short + negative radius
        String badJson = "{\"name\":\"A\",\"type\":\"X\",\"radiusKm\":-1,\"massKg\":1,\"orbitalPeriodDays\":1}";

        mockMvc.perform(post("/api/planets")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/planets"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void searchAndNamesEndpoints_shouldWork() throws Exception {
        planetRepository.save(Planet.builder()
                .name("TestJupiter")
                .type("Gas Giant")
                .radiusKm(69911.0)
                .massKg(1.898e27)
                .orbitalPeriodDays(4332.59)
                .build());
        planetRepository.flush();

        mockMvc.perform(get("/api/planets/search/by-type")
                        .queryParam("type", "gas giant")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'TestJupiter')]").exists());

        mockMvc.perform(get("/api/planets/names")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@ == 'TestMercury')]").exists());
    }

    @Test
    void nameAndMassEndpoint_shouldReturnExpectedFields() throws Exception {
        mockMvc.perform(get("/api/planets/fields/name-mass")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.name == 'TestMercury' && @.massKg == 3.301E23)]").exists());
    }
}
