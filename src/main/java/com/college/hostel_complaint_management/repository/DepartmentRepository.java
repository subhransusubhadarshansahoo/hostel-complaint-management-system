package com.college.hostel_complaint_management.repository;

import com.college.hostel_complaint_management.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository  extends JpaRepository<Department,Long> {
    public  boolean existsByNameIgnoreCase(String name);



    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}

