package com.example.spaceapp.dto;

import com.example.spaceapp.model.UserRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UserDto {

    private Long id;

    @NotNull
    @Size(min = 3, max = 50)
    private String username;

    @NotNull
    private UserRole role;

    public UserDto() {
    }

    public UserDto(Long id, String username, UserRole role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
