package com.college.hostel_complaint_management.dto;


 
import lombok.AllArgsConstructor;
import lombok.Getter;
 
@Getter
@AllArgsConstructor
public class DepartmentStats {
 
    private String departmentName;
    private long staffCount;
    private long totalComplaints;
    private long pendingCount;
    private long resolvedCount;
    private int resolutionPercent;
}