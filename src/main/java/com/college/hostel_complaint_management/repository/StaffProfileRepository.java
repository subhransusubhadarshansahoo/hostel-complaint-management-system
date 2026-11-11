package com.college.hostel_complaint_management.repository;

import com.college.hostel_complaint_management.entity.StaffProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffProfileRepository extends JpaRepository<StaffProfile,Long> {
    Optional<StaffProfile> findByUserId(Long userId);

    long countByDepartmentId(Long departmentId);
}
