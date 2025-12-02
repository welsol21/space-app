package com.example.spaceapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MoonDto {

    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    @Positive
    private Double diameterKm;

    @NotNull
    @Positive
    private Double orbitalPeriodDays;

    private Long planetId;

    private String planetName;

    public MoonDto() {
    }

    public MoonDto(Long id, String name, Double diameterKm, Double orbitalPeriodDays,
                   Long planetId, String planetName) {
        this.id = id;
        this.name = name;
        this.diameterKm = diameterKm;
        this.orbitalPeriodDays = orbitalPeriodDays;
        this.planetId = planetId;
        this.planetName = planetName;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getDiameterKm() {
        return diameterKm;
    }

    public Double getOrbitalPeriodDays() {
        return orbitalPeriodDays;
    }

    public Long getPlanetId() {
        return planetId;
    }

    public String getPlanetName() {
        return planetName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDiameterKm(Double diameterKm) {
        this.diameterKm = diameterKm;
    }

    public void setOrbitalPeriodDays(Double orbitalPeriodDays) {
        this.orbitalPeriodDays = orbitalPeriodDays;
    }

    public void setPlanetId(Long planetId) {
        this.planetId = planetId;
    }

    public void setPlanetName(String planetName) {
        this.planetName = planetName;
    }
}
