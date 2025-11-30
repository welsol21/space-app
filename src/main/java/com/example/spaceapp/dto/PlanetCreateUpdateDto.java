package com.example.spaceapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
