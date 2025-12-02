package com.example.universe.dto;

import com.example.universe.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private User.Role role;
}
