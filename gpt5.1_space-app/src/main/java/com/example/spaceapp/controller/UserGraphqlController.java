package com.example.spaceapp.controller;

import com.example.spaceapp.dto.UserCreateDto;
import com.example.spaceapp.dto.UserDto;
import com.example.spaceapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserGraphqlController {

    private final UserService userService;

    public UserGraphqlController(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public UserDto userById(@Argument Long id) {
        return userService.getUserById(id);
    }

    @MutationMapping
    public UserDto createUser(@Argument("input") @Valid UserCreateDto input) {
        return userService.createUser(input);
    }
}
