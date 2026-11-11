package com.college.hostel_complaint_management.repository;

import com.college.hostel_complaint_management.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HostelRepository extends JpaRepository<Hostel, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    List<Hostel> findByActiveTrue();
}