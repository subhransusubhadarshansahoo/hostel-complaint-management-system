package com.college.hostel_complaint_management.repository;

import com.college.hostel_complaint_management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository  extends JpaRepository<Role,Long> {


    public  boolean existsByName(String name);

    public Optional<Role> findByName(String name);
}
