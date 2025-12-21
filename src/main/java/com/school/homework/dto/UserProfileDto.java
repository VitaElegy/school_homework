package com.school.homework.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserProfileDto {
    private String username; // Read-only for display

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    private String confirmNewPassword;
    
    private MultipartFile avatar;
    private String currentAvatar; // For display
}

