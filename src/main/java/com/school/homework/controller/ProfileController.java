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
        
        model.addAttribute("profile", userProfileDto);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("profile") UserProfileDto userProfileDto, BindingResult bindingResult, Principal principal, Model model) {
        if (bindingResult.hasErrors()) {
            return "profile";
        }

        try {
            userService.updateUserProfile(principal.getName(), userProfileDto);
            model.addAttribute("successMessage", "Profile updated successfully!");
        } catch (IllegalArgumentException | com.school.homework.exception.DuplicateResourceException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "profile"; // Return to profile page with error
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred.");
            return "profile";
        }
        
        // Refresh DTO with potentially updated data (though username is same)
        // Ideally redirect to get pattern to avoid resubmit, but passing success message via flash attributes requires RedirectAttributes
        // For simplicity with this current setup, we just return the view with the message.
        // But to be robust, let's keep the user inputs if there was an error (already done by @ModelAttribute), 
        // if success, we can clear password fields.
        
        userProfileDto.setNewPassword("");
        userProfileDto.setConfirmNewPassword("");
        return "profile";
    }
}

