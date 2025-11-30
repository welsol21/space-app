package com.example.spaceapp.repository;

import com.example.spaceapp.model.User;
import com.example.spaceapp.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_shouldFindUser() {
        userRepository.save(User.builder()
                .username("someone")
                .password("hash")
                .role(UserRole.STUDENT)
                .build());

        assertThat(userRepository.findByUsername("someone")).isPresent();
        assertThat(userRepository.findByUsername("missing")).isNotPresent();
    }
}
