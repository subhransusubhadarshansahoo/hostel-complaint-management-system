package com.college.hostel_complaint_management.service;

import com.college.hostel_complaint_management.dto.UserRegistrationDto;
import com.college.hostel_complaint_management.service.result.RegistrationResult;

public interface UserService {

    RegistrationResult registerUser(UserRegistrationDto registrationDto);

}