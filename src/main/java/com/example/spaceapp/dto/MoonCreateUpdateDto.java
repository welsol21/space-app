package com.example.spaceapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
