package com.college.hostel_complaint_management.dto;

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

    private String email;

    private String password;

    private String phoneNo;


    private String confirmPassword;

}