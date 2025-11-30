package com.example.spaceapp.config;

import com.example.spaceapp.model.User;
import com.example.spaceapp.model.UserRole;
import com.example.spaceapp.repository.UserRepository;
import com.example.spaceapp.testutil.TestUsers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DataInitializerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void preloadsThreeUsersWithEncodedPasswordsAndRoles() {
        User admin = userRepository.findByUsername(TestUsers.ADMIN_USER).orElseThrow();
        User staff = userRepository.findByUsername(TestUsers.STAFF_USER).orElseThrow();
        User student = userRepository.findByUsername(TestUsers.STUDENT_USER).orElseThrow();

        assertThat(admin.getRole()).isEqualTo(UserRole.ADMIN);
        assertThat(staff.getRole()).isEqualTo(UserRole.STAFF);
        assertThat(student.getRole()).isEqualTo(UserRole.STUDENT);

        assertThat(passwordEncoder.matches(TestUsers.ADMIN_PASS, admin.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(TestUsers.STAFF_PASS, staff.getPassword())).isTrue();
        assertThat(passwordEncoder.matches(TestUsers.STUDENT_PASS, student.getPassword())).isTrue();

        // Raw passwords must not be stored
        assertThat(admin.getPassword()).isNotEqualTo(TestUsers.ADMIN_PASS);
        assertThat(staff.getPassword()).isNotEqualTo(TestUsers.STAFF_PASS);
        assertThat(student.getPassword()).isNotEqualTo(TestUsers.STUDENT_PASS);
    }
}
