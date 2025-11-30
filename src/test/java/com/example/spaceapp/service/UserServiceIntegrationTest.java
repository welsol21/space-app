package com.example.spaceapp.service;

import com.example.spaceapp.dto.UserCreateDto;
import com.example.spaceapp.dto.UserDto;
import com.example.spaceapp.exception.BadRequestException;
import com.example.spaceapp.exception.ResourceNotFoundException;
import com.example.spaceapp.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_andFetchById_shouldWork() {
        UserDto created = userService.createUser(UserCreateDto.builder()
                .username("newuser")
                .password("newuser123")
                .role(UserRole.STUDENT)
                .build());

        assertThat(created.getId()).isNotNull();
        assertThat(created.getUsername()).isEqualTo("newuser");
        assertThat(created.getRole()).isEqualTo(UserRole.STUDENT);

        UserDto fetched = userService.getUserById(created.getId());
        assertThat(fetched.getUsername()).isEqualTo("newuser");
    }

    @Test
    void createUser_duplicateUsername_shouldThrowBadRequest() {
        userService.createUser(UserCreateDto.builder()
                .username("dupe")
                .password("password123")
                .role(UserRole.STAFF)
                .build());

        assertThatThrownBy(() -> userService.createUser(UserCreateDto.builder()
                .username("dupe")
                .password("password123")
                .role(UserRole.STAFF)
                .build()))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void getUserById_missing_shouldThrowNotFound() {
        assertThatThrownBy(() -> userService.getUserById(999999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
