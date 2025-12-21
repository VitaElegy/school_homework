package com.school.homework.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/404";
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResourceException(DuplicateResourceException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/409"; // Or a generic error page with specific message
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public String handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e, Model model) {
        model.addAttribute("errorMessage", "Access Denied: You do not have permission to perform this action.");
        return "error/403";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        // Log the error (In a real app, use a logger)
        System.err.println("Internal Server Error: " + e.getMessage());
        e.printStackTrace();
        
        // Do NOT expose internal error details to the user in production
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        return "error/error";
    }

    // You can add more specific handlers here, e.g., for validation errors if not handled by binding result
}

