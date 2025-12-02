package com.example.spaceapp.service;

import com.example.spaceapp.dto.MoonCreateUpdateDto;
import com.example.spaceapp.dto.MoonDto;
import com.example.spaceapp.exception.ResourceNotFoundException;
import com.example.spaceapp.model.Moon;
import com.example.spaceapp.model.Planet;
import com.example.spaceapp.repository.MoonRepository;
import com.example.spaceapp.repository.PlanetRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MoonService {

    private final MoonRepository moonRepository;
    private final PlanetRepository planetRepository;

    public MoonService(MoonRepository moonRepository, PlanetRepository planetRepository) {
        this.moonRepository = moonRepository;
        this.planetRepository = planetRepository;
    }

    @Transactional(readOnly = true)
    public List<MoonDto> getAllMoons() {
        return moonRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MoonDto getMoonById(Long id) {
        Moon moon = moonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Moon not found with id " + id));
        return toDto(moon);
    }

    public MoonDto createMoon(MoonCreateUpdateDto dto) {
        Planet planet = planetRepository.findById(dto.getPlanetId())
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found with id " + dto.getPlanetId()));
        Moon moon = new Moon();
        updateEntityFromDto(moon, dto, planet);
        Moon saved = moonRepository.save(moon);
        return toDto(saved);
    }

    public MoonDto updateMoon(Long id, MoonCreateUpdateDto dto) {
        Moon moon = moonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Moon not found with id " + id));
        Planet planet = planetRepository.findById(dto.getPlanetId())
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found with id " + dto.getPlanetId()));
        updateEntityFromDto(moon, dto, planet);
        Moon saved = moonRepository.save(moon);
        return toDto(saved);
    }

    public void deleteMoon(Long id) {
        if (!moonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Moon not found with id " + id);
        }
        moonRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<MoonDto> listByPlanetName(String planetName) {
        return moonRepository.findByPlanet_NameIgnoreCase(planetName)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countByPlanetId(Long planetId) {
        return moonRepository.countByPlanet_Id(planetId);
    }

    private MoonDto toDto(Moon moon) {
        Long planetId = moon.getPlanet() != null ? moon.getPlanet().getId() : null;
        String planetName = moon.getPlanet() != null ? moon.getPlanet().getName() : null;
        return new MoonDto(
                moon.getId(),
                moon.getName(),
                moon.getDiameterKm(),
                moon.getOrbitalPeriodDays(),
                planetId,
                planetName
        );
    }

    private void updateEntityFromDto(Moon moon, MoonCreateUpdateDto dto, Planet planet) {
        moon.setName(dto.getName());
        moon.setDiameterKm(dto.getDiameterKm());
        moon.setOrbitalPeriodDays(dto.getOrbitalPeriodDays());
        moon.setPlanet(planet);
    }
}
