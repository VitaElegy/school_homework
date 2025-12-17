package com.school.homework.service;

import com.school.homework.dto.UserDto;
import com.school.homework.entity.User;

public interface UserService {
    User registerUser(User user); // Keeping Entity for input for now, ideally DTO
    User findUserByUsername(String username);
    UserDto getUserDtoByUsername(String username);
}

