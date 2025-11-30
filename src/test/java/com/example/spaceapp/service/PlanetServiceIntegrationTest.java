package com.example.spaceapp.service;

import com.example.spaceapp.dto.PlanetCreateUpdateDto;
import com.example.spaceapp.dto.PlanetDto;
import com.example.spaceapp.exception.BadRequestException;
import com.example.spaceapp.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PlanetServiceIntegrationTest {

    @Autowired
    private PlanetService planetService;

    @Test
    void createAndFetchPlanet_shouldWork() {
        PlanetCreateUpdateDto dto = PlanetCreateUpdateDto.builder()
                .name("Neptune")
                .type("Ice Giant")
                .radiusKm(24622.0)
                .massKg(1.024e26)
                .orbitalPeriodDays(60190.0)
                .build();

        PlanetDto created = planetService.createPlanet(dto);
        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Neptune");

        PlanetDto fetched = planetService.getPlanetById(created.getId());
        assertThat(fetched.getName()).isEqualTo("Neptune");
    }

    @Test
    void createPlanet_duplicateName_shouldThrowBadRequest() {
        PlanetCreateUpdateDto dto = PlanetCreateUpdateDto.builder()
                .name("Venus")
                .type("Terrestrial")
                .radiusKm(6051.8)
                .massKg(4.867e24)
                .orbitalPeriodDays(224.7)
                .build();

        planetService.createPlanet(dto);

        assertThatThrownBy(() -> planetService.createPlanet(dto))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void getPlanetById_missing_shouldThrowNotFound() {
        assertThatThrownBy(() -> planetService.getPlanetById(999999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
