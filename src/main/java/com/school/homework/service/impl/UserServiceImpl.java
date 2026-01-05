package com.school.homework.service.impl;

import com.school.homework.dao.RoleRepository;
import com.school.homework.dao.UserRepository;
import com.school.homework.dto.RegisterDto;
import com.school.homework.dto.UserDto;
import com.school.homework.dto.UserProfileDto;
import com.school.homework.entity.Role;
import com.school.homework.entity.User;
import com.school.homework.service.FileStorageService;
import com.school.homework.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder, FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public User registerUser(RegisterDto registerDto) {
        if (userRepository.findByUsername(registerDto.getUsername()).isPresent()) {
            throw new com.school.homework.exception.DuplicateResourceException("Username already exists");
        }
        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new com.school.homework.exception.DuplicateResourceException("Email already exists");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));
        user.setRoles(java.util.Collections.singleton(userRole));

        return userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    @Override
    public UserDto getUserDtoByUsername(String username) {
        User user = findUserByUsername(username);
        return convertToDto(user);
    }

    @Override
    public void updateUserInfo(String username, com.school.homework.dto.UserInfoDto userInfoDto) {
        User user = findUserByUsername(username);

        // Update Email
        if (!user.getEmail().equals(userInfoDto.getEmail())) {
            if (userRepository.findByEmail(userInfoDto.getEmail()).isPresent()) {
                throw new com.school.homework.exception.DuplicateResourceException("Email already exists");
            }
            user.setEmail(userInfoDto.getEmail());
        }

        // Update Avatar
        if (userInfoDto.getAvatar() != null && !userInfoDto.getAvatar().isEmpty()) {
            // Delete old avatar if exists
            if (user.getAvatar() != null) {
                fileStorageService.deleteFile(user.getAvatar());
            }
            String fileName = fileStorageService.saveFile(userInfoDto.getAvatar());
            user.setAvatar(fileName);
        }

        userRepository.save(user);
    }

    @Override
    public void changeUserPassword(String username, com.school.homework.dto.UserPasswordDto userPasswordDto) {
        User user = findUserByUsername(username);

        if (!userPasswordDto.getNewPassword().equals(userPasswordDto.getConfirmNewPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(userPasswordDto.getNewPassword()));
        userRepository.save(user);
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
