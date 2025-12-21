package com.school.homework.service;

import com.school.homework.dto.RegisterDto;
import com.school.homework.dto.UserDto;
import com.school.homework.dto.UserProfileDto;
import com.school.homework.entity.User;

public interface UserService {
    User registerUser(RegisterDto registerDto);
    User findUserByUsername(String username);
    UserDto getUserDtoByUsername(String username);
    void updateUserProfile(String username, UserProfileDto userProfileDto);
}

