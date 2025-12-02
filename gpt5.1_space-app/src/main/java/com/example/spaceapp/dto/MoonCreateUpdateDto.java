package com.example.spaceapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MoonCreateUpdateDto {

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @NotNull
    @Positive
    private Double diameterKm;

    @NotNull
    @Positive
    private Double orbitalPeriodDays;

    @NotNull
    @Positive
    private Long planetId;

    public MoonCreateUpdateDto() {
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
}
