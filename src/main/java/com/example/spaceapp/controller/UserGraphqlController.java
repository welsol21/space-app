package com.example.spaceapp.controller;

import com.example.spaceapp.dto.UserCreateDto;
import com.example.spaceapp.dto.UserDto;
import com.example.spaceapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

@Controller
/** GraphQL controller for admin-only user queries and mutations */
public class UserGraphqlController {
    private final UserService userService;

    public UserGraphqlController(UserService userService) {
        this.userService = userService;
    }

    /** Query: fetch user by id (ADMIN) */
    @QueryMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto userById(@Argument Long id) {
        return userService.getUserById(id);
    }

    /** Mutation: create user (ADMIN) */
    @MutationMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto createUser(@Argument("input") @Valid UserCreateDto input) {
        return userService.createUser(input);
    }
}
