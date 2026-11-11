package com.college.hostel_complaint_management.service;



import com.college.hostel_complaint_management.dto.DepartmentStats;
import com.college.hostel_complaint_management.enums.ComplaintStatus;
import com.college.hostel_complaint_management.repository.ComplaintRepository;
import com.college.hostel_complaint_management.repository.DepartmentRepository;
import com.college.hostel_complaint_management.repository.StaffProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final DepartmentRepository departmentRepository;
    private final ComplaintRepository complaintRepository;
    private final StaffProfileRepository staffProfileRepository;

    public DashboardService(DepartmentRepository departmentRepository,
                            ComplaintRepository complaintRepository,
                            StaffProfileRepository staffProfileRepository) {
        this.departmentRepository = departmentRepository;
        this.complaintRepository = complaintRepository;
        this.staffProfileRepository = staffProfileRepository;
    }

    public List<DepartmentStats> getDepartmentStats() {

        return departmentRepository.findByActiveTrue().stream()
                .map(department -> {

                    Long id = department.getId();

                    long total = complaintRepository.countByCategoryDepartmentId(id);
                    long pending = complaintRepository
                            .countByCategoryDepartmentIdAndStatus(id, ComplaintStatus.PENDING);
                    long resolved = complaintRepository
                            .countByCategoryDepartmentIdAndStatus(id, ComplaintStatus.RESOLVED);
                    long staff = staffProfileRepository.countByDepartmentId(id);

                    int percent = total == 0 ? 0 : (int) (resolved * 100 / total);

                    return new DepartmentStats(
                            department.getName(), staff, total, pending, resolved, percent);
                })
                .toList();
    }
}
