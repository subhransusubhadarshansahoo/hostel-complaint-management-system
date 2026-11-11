package com.college.hostel_complaint_management.repository;

import com.college.hostel_complaint_management.entity.ComplaintCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintCategoryRepository
        extends JpaRepository<ComplaintCategory, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<ComplaintCategory> findByActiveTrue();

    boolean existsByIdAndActiveTrue(Long id);
}