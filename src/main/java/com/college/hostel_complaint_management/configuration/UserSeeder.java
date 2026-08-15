package com.college.hostel_complaint_management.configuration;

import com.college.hostel_complaint_management.entity.Role;
import com.college.hostel_complaint_management.entity.User;
import com.college.hostel_complaint_management.exception.RoleNotFoundException;
import com.college.hostel_complaint_management.repository.RoleRepository;
import com.college.hostel_complaint_management.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder  implements CommandLineRunner {

     private final UserRepository  userRepository;
     private final RoleRepository roleRepository;

     private final PasswordEncoder passwordEncoder;


    public UserSeeder(UserRepository userRepository, RoleRepository roleRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        Role role = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RoleNotFoundException("GIVEN ROLE IS NOT AVAILABLE"));
        if (!userRepository.existsByRoleId(role.getId())) {

            User user = new User();

            user.setName("priyansu sahoo");
            user.setEmail("priyansu123@gmail.com");
            user.setEnabled(true);
            user.setPassword(passwordEncoder.encode("ronak123"));
            user.setPhone("7077283335");
            user.setRole(role);

            userRepository.save(user);
        }






    }
}
