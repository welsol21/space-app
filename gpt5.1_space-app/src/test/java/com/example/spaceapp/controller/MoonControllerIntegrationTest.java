package com.example.spaceapp.controller;

import com.example.spaceapp.dto.MoonCreateUpdateDto;
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

    private String basic(String u, String p) {
        String token = u + ":" + p;
        return "Basic " + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8));
    }

    @BeforeEach
    void setup() {
        Long id = planetRepository.findByNameIgnoreCase("Earth").map(com.example.spaceapp.model.Planet::getId).orElseThrow();
        planetId = id;
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

        MoonCreateUpdateDto dto = new MoonCreateUpdateDto();
                dto.setName("Luna");
                dto.setDiameterKm(3474.8);
                dto.setOrbitalPeriodDays(27.3);
                dto.setPlanetId(planetId);

        mockMvc.perform(post("/api/moons")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STUDENT_USER, TestUsers.STUDENT_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void staff_canCreateUpdateDelete_andQueries() throws Exception {
        MoonCreateUpdateDto dto = new MoonCreateUpdateDto();
                dto.setName("Luna");
                dto.setDiameterKm(3474.8);
                dto.setOrbitalPeriodDays(27.3);
                dto.setPlanetId(planetId);

        String response = mockMvc.perform(post("/api/moons")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Luna"))
                .andReturn().getResponse().getContentAsString();

        long moonId = Long.parseLong(response.replaceAll(".*\"id\"\\s*:\\s*(\\d+).*", "$1"));

        MoonCreateUpdateDto update = new MoonCreateUpdateDto();
                update.setName("Luna");
                update.setDiameterKm(3475.0);
                update.setOrbitalPeriodDays(27.3);
                update.setPlanetId(planetId);

        mockMvc.perform(put("/api/moons/" + moonId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.toJson(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diameterKm").value(3475.0));

        mockMvc.perform(get("/api/moons/by-planet-name/Earth")
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].planetName").value("Earth"));

        mockMvc.perform(get("/api/moons/count/by-planet/" + planetId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.ADMIN_USER, TestUsers.ADMIN_PASS)))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.matchesRegex("\\d+")));

        mockMvc.perform(delete("/api/moons/" + moonId)
                        .header(HttpHeaders.AUTHORIZATION, basic(TestUsers.STAFF_USER, TestUsers.STAFF_PASS)))
                .andExpect(status().isNoContent());
    }
}
