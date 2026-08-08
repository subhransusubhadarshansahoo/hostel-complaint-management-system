package com.college.hostel_complaint_management.service.serviceimp;

import com.college.hostel_complaint_management.dto.UserRegistrationDto;
import com.college.hostel_complaint_management.entity.Role;
import com.college.hostel_complaint_management.entity.User;
import com.college.hostel_complaint_management.repository.RoleRepository;
import com.college.hostel_complaint_management.repository.UserRepository;

import com.college.hostel_complaint_management.service.RegistrationResult;
import com.college.hostel_complaint_management.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplementation  implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

   private final PasswordEncoder passwordEncoder;


    public UserServiceImplementation(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public RegistrationResult registerUser(UserRegistrationDto registrationDto) {

        if(userRepository.existsByEmail(registrationDto.getEmail())){
            return  RegistrationResult.EMAIL_EXISTS;
        }


        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Student role not found"));

        User user = new User();

        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPhone(registrationDto.getPhoneNo());

        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        user.setRole(studentRole);

        user.setEnabled(true);

        userRepository.save(user);

        return  RegistrationResult.SUCCESS;
    }


    }

