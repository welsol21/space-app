package com.example.universe.service;

import com.example.universe.dto.MoonDTO;
import com.example.universe.entity.Moon;
import com.example.universe.entity.Planet;
import com.example.universe.exception.ResourceNotFoundException;
import com.example.universe.repository.MoonRepository;
import com.example.universe.repository.PlanetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoonService {
    private final MoonRepository moonRepository;
    private final PlanetRepository planetRepository;

    public List<MoonDTO> findAll() {
        return moonRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public MoonDTO findById(Long id) {
        return moonRepository.findById(id).map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Moon not found: " + id));
    }
    
    public List<MoonDTO> findByPlanetName(String planetName) {
        return moonRepository.findByPlanetNameIgnoreCase(planetName).stream()
                .map(this::toDTO).collect(Collectors.toList());
    }
    
    public long countByPlanetId(Long planetId) {
        if (!planetRepository.existsById(planetId)) {
            throw new ResourceNotFoundException("Planet not found: " + planetId);
        }
        return moonRepository.countByPlanetId(planetId);
    }

    public MoonDTO create(MoonDTO dto) {
        Planet planet = planetRepository.findById(dto.getPlanetId())
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + dto.getPlanetId()));
        
        Moon moon = Moon.builder()
                .name(dto.getName())
                .diameterKm(dto.getDiameterKm())
                .orbitalPeriodDays(dto.getOrbitalPeriodDays())
                .planet(planet)
                .build();
        
        return toDTO(moonRepository.save(moon));
    }
    
    public void delete(Long id) {
        if (!moonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Moon not found: " + id);
        }
        moonRepository.deleteById(id);
    }

    private MoonDTO toDTO(Moon m) {
        return MoonDTO.builder()
                .id(m.getId())
                .name(m.getName())
                .diameterKm(m.getDiameterKm())
                .orbitalPeriodDays(m.getOrbitalPeriodDays())
                .planetId(m.getPlanet().getId())
                .build();
    }
}
