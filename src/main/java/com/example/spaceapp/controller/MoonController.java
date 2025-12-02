package com.example.spaceapp.controller;

import com.example.spaceapp.dto.MoonCreateUpdateDto;
import com.example.spaceapp.dto.MoonDto;
import com.example.spaceapp.service.MoonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moons")
/** REST controller for moon endpoints. Read allowed to ADMIN/STAFF/STUDENT; write restricted to ADMIN/STAFF. */
public class MoonController {
    private final MoonService moonService;

    public MoonController(MoonService moonService) {
        this.moonService = moonService;
    }

    /** List all moons */
    @GetMapping
    public List<MoonDto> getAll() {
        return moonService.getAllMoons();
    }

    /** Get a moon by id */
    @GetMapping("/{id}")
    public MoonDto getById(@PathVariable Long id) {
        return moonService.getMoonById(id);
    }

    /** Create moon (ADMIN/STAFF) */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public MoonDto create(@Valid @RequestBody MoonCreateUpdateDto dto) {
        return moonService.createMoon(dto);
    }

    /** Update moon (ADMIN/STAFF) */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public MoonDto update(@PathVariable Long id, @Valid @RequestBody MoonCreateUpdateDto dto) {
        return moonService.updateMoon(id, dto);
    }

    /** Delete moon (ADMIN/STAFF) */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    public void delete(@PathVariable Long id) {
        moonService.deleteMoon(id);
    }

    /** List moons by planet name */
    @GetMapping("/by-planet-name/{planetName}")
    public List<MoonDto> listByPlanetName(@PathVariable String planetName) {
        return moonService.listByPlanetName(planetName);
    }

    /** Count moons by planet id */
    @GetMapping("/count/by-planet/{planetId}")
    public long countByPlanet(@PathVariable Long planetId) {
        return moonService.countByPlanetId(planetId);
    }
}
