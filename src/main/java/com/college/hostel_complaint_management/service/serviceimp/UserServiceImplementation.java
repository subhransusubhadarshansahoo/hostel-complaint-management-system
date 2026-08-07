package com.college.hostel_complaint_management.service.serviceimp;

import com.college.hostel_complaint_management.dto.UserRegistrationDto;
import com.college.hostel_complaint_management.entity.Role;
import com.college.hostel_complaint_management.entity.User;
import com.college.hostel_complaint_management.repository.RoleRepository;
import com.college.hostel_complaint_management.repository.UserRepository;

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
    public void registerUser(UserRegistrationDto registrationDto) {

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if(!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())){
            throw  new RuntimeException("password mismatch");}
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
    }
}
