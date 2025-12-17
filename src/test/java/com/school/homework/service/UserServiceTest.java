package com.school.homework.service;

import com.school.homework.dao.RoleRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.entity.Role;
import com.school.homework.entity.User;
import com.school.homework.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role role;

    @BeforeEach
    public void setup() {
        user = new User(1L, "testuser", "password", "test@example.com", LocalDateTime.now(), null, new HashSet<>());
        role = new Role(1L, "ROLE_USER", new HashSet<>());
    }

    @Test
    public void whenRegisterUser_thenReturnUser() {
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.empty());
        given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.of(role));
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(user)).willReturn(user);

        User createdUser = userService.registerUser(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo(user.getUsername());
        assertThat(createdUser.getRoles()).contains(role);
    }

    @Test
    public void whenRegisterExistingUser_thenThrowException() {
        given(userRepository.findByUsername(user.getUsername())).willReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.registerUser(user));
    }
}

