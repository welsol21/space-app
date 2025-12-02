package com.example.universe.service;

import com.example.universe.dto.CreateUserDTO;
import com.example.universe.dto.UserDTO;
import com.example.universe.entity.User;
import com.example.universe.exception.ResourceNotFoundException;
import com.example.universe.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserDTO findById(Long id) {
        return repository.findById(id)
                .map(u -> UserDTO.builder()
                        .id(u.getId())
                        .username(u.getUsername())
                        .role(u.getRole())
                        .build())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserDTO createUser(CreateUserDTO input) {
        User user = User.builder()
                .username(input.getUsername())
                .password(passwordEncoder.encode(input.getPassword()))
                .role(input.getRole())
                .build();
        
        User saved = repository.save(user);
        return UserDTO.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .role(saved.getRole())
                .build();
    }
}
