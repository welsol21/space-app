package com.example.universe.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "moons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Moon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double diameterKm;
    private Double orbitalPeriodDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "planet_id")
    @ToString.Exclude
    private Planet planet;
}
