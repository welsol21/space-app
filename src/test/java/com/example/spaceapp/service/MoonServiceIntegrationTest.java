package com.example.spaceapp.service;

import com.example.spaceapp.dto.MoonCreateUpdateDto;
import com.example.spaceapp.dto.MoonDto;
import com.example.spaceapp.dto.PlanetCreateUpdateDto;
import com.example.spaceapp.dto.PlanetDto;
import com.example.spaceapp.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class MoonServiceIntegrationTest {

    @Autowired
    private PlanetService planetService;

    @Autowired
    private MoonService moonService;

    @Test
    void createMoon_shouldMapPlanetFields() {
        PlanetDto planet = planetService.createPlanet(PlanetCreateUpdateDto.builder()
                .name("Earth")
                .type("Terrestrial")
                .radiusKm(6371.0)
                .massKg(5.972e24)
                .orbitalPeriodDays(365.25)
                .build());

        MoonDto created = moonService.createMoon(MoonCreateUpdateDto.builder()
                .name("Moon")
                .diameterKm(3474.8)
                .orbitalPeriodDays(27.3)
                .planetId(planet.getId())
                .build());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getPlanetId()).isEqualTo(planet.getId());
        assertThat(created.getPlanetName()).isEqualTo("Earth");
    }

    @Test
    void createMoon_withMissingPlanet_shouldThrowNotFound() {
        assertThatThrownBy(() -> moonService.createMoon(MoonCreateUpdateDto.builder()
                .name("GhostMoon")
                .diameterKm(1.0)
                .orbitalPeriodDays(1.0)
                .planetId(999999L)
                .build()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void countByPlanetId_shouldMatchRepositoryCount() {
        PlanetDto mars = planetService.createPlanet(PlanetCreateUpdateDto.builder()
                .name("Mars")
                .type("Terrestrial")
                .radiusKm(3389.5)
                .massKg(6.39e23)
                .orbitalPeriodDays(687.0)
                .build());

        moonService.createMoon(MoonCreateUpdateDto.builder()
                .name("Phobos")
                .diameterKm(22.2)
                .orbitalPeriodDays(0.3189)
                .planetId(mars.getId())
                .build());
        moonService.createMoon(MoonCreateUpdateDto.builder()
                .name("Deimos")
                .diameterKm(12.4)
                .orbitalPeriodDays(1.263)
                .planetId(mars.getId())
                .build());

        assertThat(moonService.countByPlanetId(mars.getId())).isEqualTo(2);
    }
}
