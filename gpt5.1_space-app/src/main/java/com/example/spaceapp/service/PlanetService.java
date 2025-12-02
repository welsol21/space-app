package com.example.spaceapp.service;

import com.example.spaceapp.dto.PlanetCreateUpdateDto;
import com.example.spaceapp.dto.PlanetDto;
import com.example.spaceapp.exception.ResourceNotFoundException;
import com.example.spaceapp.model.Planet;
import com.example.spaceapp.repository.PlanetRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlanetService {

    private final PlanetRepository planetRepository;

    public PlanetService(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    @Transactional(readOnly = true)
    public List<PlanetDto> getAllPlanets() {
        return planetRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlanetDto getPlanetById(Long id) {
        Planet planet = planetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found with id " + id));
        return toDto(planet);
    }

    public PlanetDto createPlanet(PlanetCreateUpdateDto dto) {
        Planet planet = new Planet();
        updateEntityFromDto(planet, dto);
        Planet saved = planetRepository.save(planet);
        return toDto(saved);
    }

    public PlanetDto updatePlanet(Long id, PlanetCreateUpdateDto dto) {
        Planet planet = planetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found with id " + id));
        updateEntityFromDto(planet, dto);
        Planet saved = planetRepository.save(planet);
        return toDto(saved);
    }

    public void deletePlanet(Long id) {
        if (!planetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Planet not found with id " + id);
        }
        planetRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PlanetDto> findByType(String type) {
        return planetRepository.findByTypeIgnoreCase(type)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getAllNames() {
        return planetRepository.findAllNames();
    }

    private PlanetDto toDto(Planet planet) {
        return new PlanetDto(
                planet.getId(),
                planet.getName(),
                planet.getType(),
                planet.getRadiusKm(),
                planet.getMassKg(),
                planet.getOrbitalPeriodDays()
        );
    }

    private void updateEntityFromDto(Planet planet, PlanetCreateUpdateDto dto) {
        planet.setName(dto.getName());
        planet.setType(dto.getType());
        planet.setRadiusKm(dto.getRadiusKm());
        planet.setMassKg(dto.getMassKg());
        planet.setOrbitalPeriodDays(dto.getOrbitalPeriodDays());
    }
}
