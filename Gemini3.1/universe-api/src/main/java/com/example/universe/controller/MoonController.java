package com.example.universe.controller;

import com.example.universe.dto.MoonDTO;
import com.example.universe.service.MoonService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/moons")
@RequiredArgsConstructor
public class MoonController {

    private final MoonService service;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all moons or filter by planet name")
    public List<MoonDTO> getMoons(@RequestParam(required = false) String planetName) {
        if (planetName != null && !planetName.isEmpty()) {
            return service.findByPlanetName(planetName);
        }
        return service.findAll();
    }
    
    @GetMapping("/count")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Count moons for a specific planet")
    public Long countMoons(@RequestParam Long planetId) {
        return service.countByPlanetId(planetId);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MoonDTO getMoon(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MoonDTO createMoon(@Valid @RequestBody MoonDTO dto) {
        return service.create(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMoon(@PathVariable Long id) {
        service.delete(id);
    }
}
