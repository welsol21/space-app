package com.example.spaceapp.controller;

import com.example.spaceapp.dto.MoonCreateUpdateDto;
import com.example.spaceapp.dto.MoonDto;
import com.example.spaceapp.service.MoonService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moons")
public class MoonController {
    private final MoonService moonService;

    public MoonController(MoonService moonService) {
        this.moonService = moonService;
    }

    @GetMapping
    public List<MoonDto> getAll() {
        return moonService.getAllMoons();
    }

    @GetMapping("/{id}")
    public MoonDto getById(@PathVariable Long id) {
        return moonService.getMoonById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MoonDto create(@Valid @RequestBody MoonCreateUpdateDto dto) {
        return moonService.createMoon(dto);
    }

    @PutMapping("/{id}")
    public MoonDto update(@PathVariable Long id, @Valid @RequestBody MoonCreateUpdateDto dto) {
        return moonService.updateMoon(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        moonService.deleteMoon(id);
    }

    @GetMapping("/by-planet-name/{planetName}")
    public List<MoonDto> listByPlanetName(@PathVariable String planetName) {
        return moonService.listByPlanetName(planetName);
    }

    @GetMapping("/count/by-planet/{planetId}")
    public long countByPlanet(@PathVariable Long planetId) {
        return moonService.countByPlanetId(planetId);
    }
}
