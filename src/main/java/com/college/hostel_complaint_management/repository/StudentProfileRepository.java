package com.college.hostel_complaint_management.repository;

import com.college.hostel_complaint_management.entity.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository extends JpaRepository<StudentProfile,Long> {
}
