package com.college.hostel_complaint_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserRegistrationDto {

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z ]*$",
            message = "Name must start with a letter and contain only letters and spaces"
    )
    private String name;


    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email address")
    private String email;



    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Enter a valid 10-digit mobile number"
    )
    private String phoneNo;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    private String password;

    @NotBlank(message ="confirm the password")
    private String confirmPassword;

}