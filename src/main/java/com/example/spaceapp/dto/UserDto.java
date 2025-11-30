package com.example.spaceapp.dto;

import com.example.spaceapp.model.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @NotNull
    private UserRole role;
}
