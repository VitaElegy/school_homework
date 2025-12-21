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
        UserProfileDto userProfileDto = new UserProfileDto();
        userProfileDto.setUsername(user.getUsername());
        userProfileDto.setEmail(user.getEmail());
        userProfileDto.setCurrentAvatar(user.getAvatar());

        model.addAttribute("profile", userProfileDto);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profile") UserProfileDto userProfileDto, BindingResult bindingResult, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
            // Need to reload current avatar as file input is cleared
            User user = userService.findUserByUsername(principal.getName());
            userProfileDto.setCurrentAvatar(user.getAvatar());
            return "profile";
        }

        try {
            userService.updateUserProfile(principal.getName(), userProfileDto);
            model.addAttribute("successMessage", "Profile updated successfully!");
        } catch (IllegalArgumentException | com.school.homework.exception.DuplicateResourceException e) {
            model.addAttribute("errorMessage", e.getMessage());
            // Need to reload current avatar
            User user = userService.findUserByUsername(principal.getName());
            userProfileDto.setCurrentAvatar(user.getAvatar());
            return "profile";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
            // Need to reload current avatar
            User user = userService.findUserByUsername(principal.getName());
            userProfileDto.setCurrentAvatar(user.getAvatar());
            return "profile";
        }

        userProfileDto.setNewPassword("");
        userProfileDto.setConfirmNewPassword("");

        // Reload avatar after success
        User updatedUser = userService.findUserByUsername(principal.getName());
        userProfileDto.setCurrentAvatar(updatedUser.getAvatar());

        return "profile";
    }
}

