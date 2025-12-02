package com.example.universe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanetDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Type is required")
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
}
