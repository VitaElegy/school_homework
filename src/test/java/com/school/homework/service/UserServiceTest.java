package com.school.homework.service;

import com.school.homework.dao.RoleRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.dto.RegisterDto;
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
import static org.mockito.ArgumentMatchers.any;
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
    private RegisterDto registerDto;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        user.setRoles(new HashSet<>());

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        role.setPermissions(new HashSet<>());

        registerDto = new RegisterDto();
        registerDto.setUsername("testuser");
        registerDto.setPassword("password");
        registerDto.setEmail("test@example.com");
    }

    @Test
    public void whenRegisterUser_thenReturnUser() {
        given(userRepository.findByUsername(registerDto.getUsername())).willReturn(Optional.empty());
        given(roleRepository.findByName("ROLE_USER")).willReturn(Optional.of(role));
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);

        User createdUser = userService.registerUser(registerDto);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo(registerDto.getUsername());
    }

    @Test
    public void whenRegisterExistingUser_thenThrowException() {
        given(userRepository.findByUsername(registerDto.getUsername())).willReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> userService.registerUser(registerDto));
    }
}
