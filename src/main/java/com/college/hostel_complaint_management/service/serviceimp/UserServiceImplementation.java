package com.college.hostel_complaint_management.service.serviceimp;

import com.college.hostel_complaint_management.dto.UserRegistrationDto;
import com.college.hostel_complaint_management.entity.Hostel;
import com.college.hostel_complaint_management.entity.Role;
import com.college.hostel_complaint_management.entity.StudentProfile;
import com.college.hostel_complaint_management.entity.User;
import com.college.hostel_complaint_management.repository.HostelRepository;
import com.college.hostel_complaint_management.repository.RoleRepository;
import com.college.hostel_complaint_management.repository.StudentProfileRepository;
import com.college.hostel_complaint_management.repository.UserRepository;

import com.college.hostel_complaint_management.service.result.RegistrationResult;
import com.college.hostel_complaint_management.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service

public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final HostelRepository hostelRepository;
     private final StudentProfileRepository studentProfileRepository;

    public UserServiceImplementation(UserRepository userRepository,
                                     RoleRepository roleRepository,
                                     PasswordEncoder passwordEncoder,
                                     HostelRepository hostelRepository,
                                     StudentProfileRepository studentProfileRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.hostelRepository = hostelRepository;
        this.studentProfileRepository=studentProfileRepository;
    }

     @Override
   @Transactional
    public RegistrationResult registerUser(UserRegistrationDto registrationDto) {

        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            return RegistrationResult.EMAIL_EXISTS;
        }

        Role studentRole = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new RuntimeException("Student role not found"));

        Hostel hostel = hostelRepository.findById(registrationDto.getHostelId())
                .orElseThrow(() -> new RuntimeException("Selected hostel not found"));

        User user = new User();

        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPhone(registrationDto.getPhoneNo());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(studentRole);
        user.setEnabled(true);

        userRepository.save(user);


         System.out.println(user.getId());


        StudentProfile profile = new StudentProfile();

        profile.setUser(user);
        profile.setHostel(hostel);
        profile.setRoomNumber(registrationDto.getRoomNumber());

        studentProfileRepository.save(profile);

        return RegistrationResult.SUCCESS;
    }


}

