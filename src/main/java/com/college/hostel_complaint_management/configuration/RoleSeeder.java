package com.college.hostel_complaint_management.configuration;

import com.college.hostel_complaint_management.entity.Role;
import com.college.hostel_complaint_management.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RoleSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public RoleSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        createRole("ADMIN");
        createRole("STAFF");
        createRole("STUDENT");
    }

    private void createRole(String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        }
    }
}
