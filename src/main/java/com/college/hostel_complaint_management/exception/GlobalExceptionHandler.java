package com.college.hostel_complaint_management.exception;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(
            ResourceNotFoundException ex,
            Model model,
            Authentication authentication) {

        model.addAttribute("errorMessage", ex.getMessage());

        if (authentication != null) {
            model.addAttribute("role",
                    authentication.getAuthorities()
                            .iterator()
                            .next()
                            .getAuthority());
        }

        return "error";
    }
}
