package com.example.spaceapp.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planet")
public class Planet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(name = "radius_km", nullable = false)
    private Double radiusKm;

    @Column(name = "mass_kg", nullable = false)
    private Double massKg;

    @Column(name = "orbital_period_days", nullable = false)
    private Double orbitalPeriodDays;

    @OneToMany(mappedBy = "planet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Moon> moons = new ArrayList<>();

    public Planet() {
    }

    public Planet(String name, String type, Double radiusKm, Double massKg, Double orbitalPeriodDays) {
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

    public List<Moon> getMoons() {
        return moons;
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

    public void setMoons(List<Moon> moons) {
        this.moons = moons;
    }
}
