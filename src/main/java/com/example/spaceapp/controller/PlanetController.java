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
/** REST controller for planet endpoints. Read allowed to ADMIN/STAFF/STUDENT; write restricted to ADMIN/STAFF. */
public class PlanetController {
    private final PlanetService planetService;

    public PlanetController(PlanetService planetService) {
        this.planetService = planetService;
    }

    /** List all planets */
    @GetMapping
    public List<PlanetDto> getAll() {
        return planetService.getAllPlanets();
    }

    /** Get a planet by id */
    @GetMapping("/{id}")
    public PlanetDto getById(@PathVariable Long id) {
        return planetService.getPlanetById(id);
    }

    /** Create planet (ADMIN/STAFF) */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public PlanetDto create(@Valid @RequestBody PlanetCreateUpdateDto dto) {
        return planetService.createPlanet(dto);
    }

    /** Update planet (ADMIN/STAFF) */
    @PutMapping("/{id}")
    public PlanetDto update(@PathVariable Long id, @Valid @RequestBody PlanetCreateUpdateDto dto) {
        return planetService.updatePlanet(id, dto);
    }

    /** Delete planet (ADMIN/STAFF) */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void delete(@PathVariable Long id) {
        planetService.deletePlanet(id);
    }

    /** Filter planets by type */
    @GetMapping("/search/by-type")
    public List<PlanetDto> findByType(@RequestParam String type) {
        return planetService.findByType(type);
    }

    /** Get all planet names */
    @GetMapping("/names")
    public List<String> getNames() {
        return planetService.getAllNames();
    }

    /** Specific fields endpoint: name and massKg */
    @GetMapping("/fields/name-mass")
    public List<com.example.spaceapp.dto.PlanetNameMassDto> getNameAndMass() {
        return planetService.getNameAndMass();
    }
}
