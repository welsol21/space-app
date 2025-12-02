package com.example.spaceapp.controller;

import com.example.spaceapp.dto.MoonCreateUpdateDto;
import com.example.spaceapp.model.Moon;
import com.example.spaceapp.model.Planet;
import com.example.spaceapp.repository.MoonRepository;
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
class MoonControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlanetRepository planetRepository;

    @Autowired
    private MoonRepository moonRepository;

    private Long planetId;
    private Long moonId;

    private String basic(String u, String p) {
        String token = u + ":" + p;
        return "Basic " + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void setup() {
        Planet earth = planetRepository.save(Planet.builder()
                .name("TestEarth")
                .type("Terrestrial")
                .radiusKm(6371.0)
                .massKg(5.972e24)
                .orbitalPeriodDays(365.25)
                .build());
        planetRepository.flush();
        planetId = earth.getId();

        Moon moon = moonRepository.save(Moon.builder()
                .name("TestMoon")
                .diameterKm(3474.8)
                .orbitalPeriodDays(27.3)
                .planet(earth)
                .build());
        moonRepository.flush();
        moonId = moon.getId();
    }

    @Test
    void student_canReadButCannotWrite() throws Exception {
        mockMvc.perform(get("/api/moons")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS)))
                .andExpect(status().isOk());

        MoonCreateUpdateDto create = MoonCreateUpdateDto.builder()
                .name("Europa")
                .diameterKm(3121.6)
                .orbitalPeriodDays(3.551)
                .planetId(planetId)
                .build();

        mockMvc.perform(post("/api/moons")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(create)))
                .andExpect(status().isForbidden());
    }

    @Test
    void staff_canCreateUpdateDelete() throws Exception {
        MoonCreateUpdateDto create = MoonCreateUpdateDto.builder()
                .name("NewMoon")
                .diameterKm(10.0)
                .orbitalPeriodDays(10.0)
                .planetId(planetId)
                .build();

        String response = mockMvc.perform(post("/api/moons")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.planetId").value(planetId))
                .andExpect(jsonPath("$.planetName").value("TestEarth"))
                .andReturn().getResponse().getContentAsString();

        long createdId = Long.parseLong(response.replaceAll(".*\"id\"\s*:\s*(\\d+).*", "$1"));

        MoonCreateUpdateDto update = MoonCreateUpdateDto.builder()
                .name("NewMoon")
                .diameterKm(11.0)
                .orbitalPeriodDays(11.0)
                .planetId(planetId)
                .build();

        mockMvc.perform(put("/api/moons/" + createdId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diameterKm").value(11.0));

        mockMvc.perform(delete("/api/moons/" + createdId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS)))
                .andExpect(status().isNoContent());
    }

    @Test
    void listAndCountEndpoints_shouldWork() throws Exception {
        mockMvc.perform(get("/api/moons/by-planet-name/testearth")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("TestMoon"))
                .andExpect(jsonPath("$[0].planetName").value("TestEarth"));

        mockMvc.perform(get("/api/moons/count/by-planet/" + planetId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    void getMoonById_missing_shouldReturn404_ErrorResponse() throws Exception {
        mockMvc.perform(get("/api/moons/999999")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/moons/999999"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
