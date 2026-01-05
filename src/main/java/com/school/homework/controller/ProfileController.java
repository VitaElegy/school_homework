package com.school.homework.controller;

import com.school.homework.dto.UserProfileDto;
import com.school.homework.entity.User;
import com.school.homework.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Principal principal) {
        User user = userService.findUserByUsername(principal.getName());

        // Prepare Info DTO
        com.school.homework.dto.UserInfoDto userInfoDto = new com.school.homework.dto.UserInfoDto();
        userInfoDto.setUsername(user.getUsername());
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setCurrentAvatar(user.getAvatar());
        model.addAttribute("profile", userInfoDto);

        // Prepare Password DTO
        model.addAttribute("passwordChange", new com.school.homework.dto.UserPasswordDto());

        return "profile";
    }

    @PostMapping("/profile/info")
    public String updateProfileInfo(@Valid @ModelAttribute("profile") com.school.homework.dto.UserInfoDto userInfoDto,
            BindingResult bindingResult,
            Principal principal,
            Model model) {
        if (bindingResult.hasErrors()) {
            User user = userService.findUserByUsername(principal.getName());
            userInfoDto.setCurrentAvatar(user.getAvatar()); // Reload avatar for display
            userInfoDto.setUsername(user.getUsername()); // Ensure username is set
            model.addAttribute("passwordChange", new com.school.homework.dto.UserPasswordDto());
            return "profile";
        }

        try {
            userService.updateUserInfo(principal.getName(), userInfoDto);
            model.addAttribute("successMessage", "Profile information updated successfully!");
        } catch (com.school.homework.exception.DuplicateResourceException e) {
            model.addAttribute("errorMessage", e.getMessage());
            User user = userService.findUserByUsername(principal.getName());
            userInfoDto.setCurrentAvatar(user.getAvatar());
            userInfoDto.setUsername(user.getUsername());
            model.addAttribute("passwordChange", new com.school.homework.dto.UserPasswordDto());
            return "profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            User user = userService.findUserByUsername(principal.getName());
            userInfoDto.setCurrentAvatar(user.getAvatar());
            userInfoDto.setUsername(user.getUsername());
            model.addAttribute("passwordChange", new com.school.homework.dto.UserPasswordDto());
            return "profile";
        }

        // Reload fresh data
        return ViewProfileWithSuccess(model, principal, "Profile updated successfully!");
    }

    @PostMapping("/profile/password")
    public String changePassword(
            @Valid @ModelAttribute("passwordChange") com.school.homework.dto.UserPasswordDto userPasswordDto,
            BindingResult bindingResult,
            Principal principal,
            Model model) {
        if (bindingResult.hasErrors()) {
            User user = userService.findUserByUsername(principal.getName());
            com.school.homework.dto.UserInfoDto userInfoDto = new com.school.homework.dto.UserInfoDto();
            userInfoDto.setUsername(user.getUsername());
            userInfoDto.setEmail(user.getEmail());
            userInfoDto.setCurrentAvatar(user.getAvatar());
            model.addAttribute("profile", userInfoDto);
            return "profile";
        }

        try {
            userService.changeUserPassword(principal.getName(), userPasswordDto);
            model.addAttribute("successMessage", "Password changed successfully!");
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            // Reload profile data
            User user = userService.findUserByUsername(principal.getName());
            com.school.homework.dto.UserInfoDto userInfoDto = new com.school.homework.dto.UserInfoDto();
            userInfoDto.setUsername(user.getUsername());
            userInfoDto.setEmail(user.getEmail());
            userInfoDto.setCurrentAvatar(user.getAvatar());
            model.addAttribute("profile", userInfoDto);
            return "profile";
        }

        return ViewProfileWithSuccess(model, principal, "Password changed successfully!");
    }

    private String ViewProfileWithSuccess(Model model, Principal principal, String message) {
        User user = userService.findUserByUsername(principal.getName());

        com.school.homework.dto.UserInfoDto userInfoDto = new com.school.homework.dto.UserInfoDto();
        userInfoDto.setUsername(user.getUsername());
        userInfoDto.setEmail(user.getEmail());
        userInfoDto.setCurrentAvatar(user.getAvatar());
        model.addAttribute("profile", userInfoDto);

        model.addAttribute("passwordChange", new com.school.homework.dto.UserPasswordDto());
        model.addAttribute("successMessage", message);
        return "profile";
    }
}
