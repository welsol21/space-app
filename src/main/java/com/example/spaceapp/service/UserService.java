package com.example.spaceapp.service;

import com.example.spaceapp.dto.UserCreateDto;
import com.example.spaceapp.dto.UserDto;
import com.example.spaceapp.exception.BadRequestException;
import com.example.spaceapp.exception.ResourceNotFoundException;
import com.example.spaceapp.model.User;
import com.example.spaceapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
/** Business logic for users, validation, and DTO mapping */
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return toDto(user);
    }

    public UserDto createUser(UserCreateDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new BadRequestException("Username already exists: " + dto.getUsername());
        }
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}
