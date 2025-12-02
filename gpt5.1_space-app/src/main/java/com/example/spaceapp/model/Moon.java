package com.example.spaceapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "moon")
public class Moon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "diameter_km", nullable = false)
    private Double diameterKm;

    @Column(name = "orbital_period_days", nullable = false)
    private Double orbitalPeriodDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id", nullable = false)
    private Planet planet;

    public Moon() {
    }

    public Moon(String name, Double diameterKm, Double orbitalPeriodDays, Planet planet) {
        this.name = name;
        this.diameterKm = diameterKm;
        this.orbitalPeriodDays = orbitalPeriodDays;
        this.planet = planet;
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

    public Planet getPlanet() {
        return planet;
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

    public void setPlanet(Planet planet) {
        this.planet = planet;
    }
}
