package com.example.spaceapp.controller;

import com.example.spaceapp.dto.PlanetCreateUpdateDto;
import com.example.spaceapp.dto.PlanetDto;
import com.example.spaceapp.service.PlanetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planets")
public class PlanetController {
    private final PlanetService planetService;

    public PlanetController(PlanetService planetService) {
        this.planetService = planetService;
    }

    @GetMapping
    public List<PlanetDto> getAll() {
        return planetService.getAllPlanets();
    }

    @GetMapping("/{id}")
    public PlanetDto getById(@PathVariable Long id) {
        return planetService.getPlanetById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public PlanetDto create(@Valid @RequestBody PlanetCreateUpdateDto dto) {
        return planetService.createPlanet(dto);
    }

    @PutMapping("/{id}")
    public PlanetDto update(@PathVariable Long id, @Valid @RequestBody PlanetCreateUpdateDto dto) {
        return planetService.updatePlanet(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void delete(@PathVariable Long id) {
        planetService.deletePlanet(id);
    }

    @GetMapping("/search/by-type")
    public List<PlanetDto> findByType(@RequestParam String type) {
        return planetService.findByType(type);
    }

    @GetMapping("/names")
    public List<String> getNames() {
        return planetService.getAllNames();
    }

    @GetMapping("/fields/name-mass")
    public List<com.example.spaceapp.dto.PlanetNameMassDto> getNameAndMass() {
        return planetService.getNameAndMass();
    }
}
