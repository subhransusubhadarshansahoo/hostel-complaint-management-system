package com.college.hostel_complaint_management.repository;

import com.college.hostel_complaint_management.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentProfileRepository extends JpaRepository<StudentProfile,Long> {





public Optional<StudentProfile> findByUserId(Long id);



}
