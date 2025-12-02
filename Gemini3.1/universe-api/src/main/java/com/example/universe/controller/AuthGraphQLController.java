package com.example.universe.controller;

import com.example.universe.dto.CreateUserDTO;
import com.example.universe.dto.UserDTO;
import com.example.universe.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuthGraphQLController {

    private final UserService userService;

    @QueryMapping
    public UserDTO userById(@Argument Long id) {
        return userService.findById(id);
    }

    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO createUser(@Argument @Valid CreateUserDTO input) {
        return userService.createUser(input);
    }
}
