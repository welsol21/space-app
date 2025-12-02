package com.example.spaceapp.service;

import com.example.spaceapp.dto.UserCreateDto;
import com.example.spaceapp.dto.UserDto;
import com.example.spaceapp.exception.BadRequestException;
import com.example.spaceapp.exception.ResourceNotFoundException;
import com.example.spaceapp.model.User;
import com.example.spaceapp.repository.UserRepository;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return toDto(user);
    }

    public UserDto createUser(UserCreateDto dto) {
        Optional<User> existing = userRepository.findByUsername(dto.getUsername());
        if (existing.isPresent()) {
            throw new BadRequestException("Username already exists: " + dto.getUsername());
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        User saved = userRepository.save(user);
        return toDto(saved);
    }

    private UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getRole());
    }
}
