package com.college.hostel_complaint_management.repository;

import com.college.hostel_complaint_management.entity.User;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User,Long> {


    public boolean existsByEmail(String email);

    public Optional<User> findByEmail(String email);
}
