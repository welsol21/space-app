package com.example.universe.service;

import com.example.universe.dto.PlanetDTO;
import com.example.universe.entity.Planet;
import com.example.universe.exception.ResourceNotFoundException;
import com.example.universe.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanetService {
    private final PlanetRepository repository;

    public List<PlanetDTO> findAll() {
        return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    public PlanetDTO findById(Long id) {
        return repository.findById(id).map(this::toDTO)
            .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + id));
    }
    
    public List<PlanetDTO> findByType(String type) {
        return repository.findByTypeIgnoreCase(type).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public PlanetDTO create(PlanetDTO dto) {
        Planet planet = toEntity(dto);
        return toDTO(repository.save(planet));
    }

    public PlanetDTO update(Long id, PlanetDTO dto) {
        Planet planet = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + id));
        planet.setName(dto.getName());
        planet.setType(dto.getType());
        planet.setMassKg(dto.getMassKg());
        planet.setRadiusKm(dto.getRadiusKm());
        planet.setOrbitalPeriodDays(dto.getOrbitalPeriodDays());
        return toDTO(repository.save(planet));
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Planet not found: " + id);
        }
        repository.deleteById(id);
    }

    private PlanetDTO toDTO(Planet p) {
        return PlanetDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .type(p.getType())
                .massKg(p.getMassKg())
                .radiusKm(p.getRadiusKm())
                .orbitalPeriodDays(p.getOrbitalPeriodDays())
                .build();
    }

    private Planet toEntity(PlanetDTO dto) {
        return Planet.builder()
                .name(dto.getName())
                .type(dto.getType())
                .massKg(dto.getMassKg())
                .radiusKm(dto.getRadiusKm())
                .orbitalPeriodDays(dto.getOrbitalPeriodDays())
                .build();
    }
}
