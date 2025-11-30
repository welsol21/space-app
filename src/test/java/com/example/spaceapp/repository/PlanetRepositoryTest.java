package com.example.spaceapp.repository;

import com.example.spaceapp.model.Planet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;

    @Test
    void findByTypeIgnoreCase_shouldMatchRegardlessOfCase() {
        planetRepository.save(Planet.builder()
                .name("Jupiter")
                .type("Gas Giant")
                .radiusKm(69911.0)
                .massKg(1.898e27)
                .orbitalPeriodDays(4332.59)
                .build());
        planetRepository.save(Planet.builder()
                .name("Saturn")
                .type("gas giant")
                .radiusKm(58232.0)
                .massKg(5.683e26)
                .orbitalPeriodDays(10759.0)
                .build());

        List<Planet> gas = planetRepository.findByTypeIgnoreCase("GAS GIANT");
        assertThat(gas).extracting(Planet::getName).containsExactlyInAnyOrder("Jupiter", "Saturn");
    }

    @Test
    void findByNameIgnoreCase_shouldFindRegardlessOfCase() {
        planetRepository.save(Planet.builder()
                .name("Earth")
                .type("Terrestrial")
                .radiusKm(6371.0)
                .massKg(5.972e24)
                .orbitalPeriodDays(365.25)
                .build());

        assertThat(planetRepository.findByNameIgnoreCase("earth")).isPresent();
        assertThat(planetRepository.findByNameIgnoreCase("EARTH")).isPresent();
    }

    @Test
    void findAllNames_shouldReturnOnlyNames() {
        planetRepository.save(Planet.builder()
                .name("Mars")
                .type("Terrestrial")
                .radiusKm(3389.5)
                .massKg(6.39e23)
                .orbitalPeriodDays(687.0)
                .build());

        List<String> names = planetRepository.findAllNames();
        assertThat(names).contains("Mars");
    }
}
