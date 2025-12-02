package com.example.universe.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "planets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Planet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type; // e.g., Terrestrial, Gas Giant
    private Double radiusKm;
    private Double massKg;
    private Double orbitalPeriodDays;

    @OneToMany(mappedBy = "planet", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Moon> moons = new ArrayList<>();
}
