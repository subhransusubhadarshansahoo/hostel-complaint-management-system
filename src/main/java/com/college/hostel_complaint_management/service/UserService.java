package com.college.hostel_complaint_management.service;

import com.college.hostel_complaint_management.dto.UserRegistrationDto;

public interface UserService {

    RegistrationResult registerUser(UserRegistrationDto registrationDto);

}