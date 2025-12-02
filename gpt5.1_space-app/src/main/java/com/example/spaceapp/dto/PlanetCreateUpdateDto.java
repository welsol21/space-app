package com.example.spaceapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PlanetCreateUpdateDto {

    @NotNull
    @Size(min = 2, max = 100)
    private String name;

    @NotNull
    @Size(min = 3, max = 50)
    private String type;

    @NotNull
    @Positive
    private Double radiusKm;

    @NotNull
    @Positive
    private Double massKg;

    @NotNull
    @Positive
    private Double orbitalPeriodDays;

    public PlanetCreateUpdateDto() {
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Double getRadiusKm() {
        return radiusKm;
    }

    public Double getMassKg() {
        return massKg;
    }

    public Double getOrbitalPeriodDays() {
        return orbitalPeriodDays;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRadiusKm(Double radiusKm) {
        this.radiusKm = radiusKm;
    }

    public void setMassKg(Double massKg) {
        this.massKg = massKg;
    }

    public void setOrbitalPeriodDays(Double orbitalPeriodDays) {
        this.orbitalPeriodDays = orbitalPeriodDays;
    }
}
