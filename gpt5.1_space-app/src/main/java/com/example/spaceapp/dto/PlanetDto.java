package com.example.spaceapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class PlanetDto {

    private Long id;

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

    public PlanetDto() {
    }

    public PlanetDto(Long id, String name, String type, Double radiusKm, Double massKg, Double orbitalPeriodDays) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.radiusKm = radiusKm;
        this.massKg = massKg;
        this.orbitalPeriodDays = orbitalPeriodDays;
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
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
