package com.example.spaceapp.dto;

import com.example.spaceapp.model.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDto {
    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @NotNull
    @Size(min = 6, max = 100)
    private String password;

    @NotNull
    private UserRole role;
}
