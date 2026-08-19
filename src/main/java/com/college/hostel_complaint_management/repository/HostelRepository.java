package com.college.hostel_complaint_management.repository;


import com.college.hostel_complaint_management.entity.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HostelRepository  extends JpaRepository<Hostel,Long> {

    public  boolean existsByName(String name);
}
