package com.school.homework.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFoundException(ResourceNotFoundException e, Model model) {
        logger.warn("Resource not found: {}", e.getMessage());
        return buildErrorPage(model, "404 Not Found", e.getMessage(), "error/404");
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public String handleDuplicateResourceException(DuplicateResourceException e, Model model) {
        logger.warn("Duplicate resource: {}", e.getMessage());
        return buildErrorPage(model, "409 Conflict", e.getMessage(), "error/409");
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public String handleAccessDeniedException(org.springframework.security.access.AccessDeniedException e, Model model) {
        logger.warn("Access denied: {}", e.getMessage());
        return buildErrorPage(model, "403 Forbidden", "Access Denied: You do not have permission to perform this action.", "error/403");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFoundException(NoResourceFoundException e, Model model) {
         // Quietly handle static resource misses or bad URLs
        return buildErrorPage(model, "404 Not Found", "The requested page was not found.", "error/404");
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        logger.error("Internal Server Error", e);
        return buildErrorPage(model, "500 Internal Server Error", "An unexpected error occurred. Please contact support if the issue persists.", "error/error");
    }

    private String buildErrorPage(Model model, String errorTitle, String errorMessage, String viewName) {
        model.addAttribute("errorTitle", errorTitle);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("timestamp", LocalDateTime.now());
        return viewName;
    }
}
