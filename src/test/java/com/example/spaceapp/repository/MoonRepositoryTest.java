package com.example.spaceapp.repository;

import com.example.spaceapp.model.Moon;
import com.example.spaceapp.model.Planet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MoonRepositoryTest {

    @Autowired
    private MoonRepository moonRepository;

    @Autowired
    private PlanetRepository planetRepository;

    @Test
    void findByPlanet_NameIgnoreCase_shouldMatchRegardlessOfCase() {
        Planet earth = planetRepository.save(Planet.builder()
                .name("Earth")
                .type("Terrestrial")
                .radiusKm(6371.0)
                .massKg(5.972e24)
                .orbitalPeriodDays(365.25)
                .build());

        moonRepository.save(Moon.builder()
                .name("Moon")
                .diameterKm(3474.8)
                .orbitalPeriodDays(27.3)
                .planet(earth)
                .build());

        assertThat(moonRepository.findByPlanet_NameIgnoreCase("earth"))
                .extracting(Moon::getName)
                .contains("Moon");
        assertThat(moonRepository.findByPlanet_NameIgnoreCase("EARTH"))
                .extracting(Moon::getName)
                .contains("Moon");
    }

    @Test
    void countByPlanet_Id_shouldCountMoons() {
        Planet mars = planetRepository.save(Planet.builder()
                .name("Mars")
                .type("Terrestrial")
                .radiusKm(3389.5)
                .massKg(6.39e23)
                .orbitalPeriodDays(687.0)
                .build());

        moonRepository.save(Moon.builder()
                .name("Phobos")
                .diameterKm(22.2)
                .orbitalPeriodDays(0.3189)
                .planet(mars)
                .build());
        moonRepository.save(Moon.builder()
                .name("Deimos")
                .diameterKm(12.4)
                .orbitalPeriodDays(1.263)
                .planet(mars)
                .build());

        assertThat(moonRepository.countByPlanet_Id(mars.getId())).isEqualTo(2);
    }
}
