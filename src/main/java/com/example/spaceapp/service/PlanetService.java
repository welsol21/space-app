package com.example.spaceapp.service;

import com.example.spaceapp.dto.PlanetCreateUpdateDto;
import com.example.spaceapp.dto.PlanetDto;
import com.example.spaceapp.exception.BadRequestException;
import com.example.spaceapp.exception.ResourceNotFoundException;
import com.example.spaceapp.model.Planet;
import com.example.spaceapp.repository.PlanetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PlanetService {
    private final PlanetRepository planetRepository;

    public PlanetService(PlanetRepository planetRepository) {
        this.planetRepository = planetRepository;
    }

    @Transactional(readOnly = true)
    public List<PlanetDto> getAllPlanets() {
        return planetRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlanetDto getPlanetById(Long id) {
        Planet planet = planetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + id));
        return toDto(planet);
    }

    public PlanetDto createPlanet(PlanetCreateUpdateDto dto) {
        // duplicate name check (case-insensitive)
        if (planetRepository.findByNameIgnoreCase(dto.getName()).isPresent()) {
            throw new BadRequestException("Planet with name already exists: " + dto.getName());
        }
        Planet planet = Planet.builder()
                .name(dto.getName())
                .type(dto.getType())
                .radiusKm(dto.getRadiusKm())
                .massKg(dto.getMassKg())
                .orbitalPeriodDays(dto.getOrbitalPeriodDays())
                .build();
        Planet saved = planetRepository.save(planet);
        return toDto(saved);
    }

    public PlanetDto updatePlanet(Long id, PlanetCreateUpdateDto dto) {
        Planet planet = planetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + id));
        updateEntityFromDto(planet, dto);
        Planet saved = planetRepository.save(planet);
        return toDto(saved);
    }

    public void deletePlanet(Long id) {
        Planet planet = planetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + id));
        planetRepository.delete(planet);
    }

    @Transactional(readOnly = true)
    public List<PlanetDto> findByType(String type) {
        return planetRepository.findByTypeIgnoreCase(type).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<String> getAllNames() {
        return planetRepository.findAllNames();
    }

    @Transactional(readOnly = true)
    public List<com.example.spaceapp.dto.PlanetNameMassDto> getNameAndMass() {
        return planetRepository.findAll().stream()
                .map(p -> new com.example.spaceapp.dto.PlanetNameMassDto(p.getName(), p.getMassKg()))
                .collect(java.util.stream.Collectors.toList());
    }

    private PlanetDto toDto(Planet planet) {
        return PlanetDto.builder()
                .id(planet.getId())
                .name(planet.getName())
                .type(planet.getType())
                .radiusKm(planet.getRadiusKm())
                .massKg(planet.getMassKg())
                .orbitalPeriodDays(planet.getOrbitalPeriodDays())
                .build();
    }

    private void updateEntityFromDto(Planet planet, PlanetCreateUpdateDto dto) {
        planet.setName(dto.getName());
        planet.setType(dto.getType());
        planet.setRadiusKm(dto.getRadiusKm());
        planet.setMassKg(dto.getMassKg());
        planet.setOrbitalPeriodDays(dto.getOrbitalPeriodDays());
    }
}
