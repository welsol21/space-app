package com.example.universe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MoonDTO {
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private Double diameterKm;

    @NotNull
    @Positive
    private Double orbitalPeriodDays;

    @NotNull(message = "Planet ID is required")
    private Long planetId;
}
