package com.college.hostel_complaint_management.repository;

import com.college.hostel_complaint_management.entity.Complaint;
import com.college.hostel_complaint_management.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository  extends JpaRepository<Complaint,Long> {

    public List<Complaint>findByStudentId(Long id);
    List<Complaint> findByCategoryDepartmentIdAndStatus(
            Long departmentId,
            ComplaintStatus status
    );


    List<Complaint> findByAssignedStaffId(Long staffId);

    boolean existsByIdAndStatus(Long complaintId, ComplaintStatus status);

//---------------->Admin  side Escalated complaint<-------------------------

    List<Complaint> findByEscalatedTrueAndAdminWarningIsNull();

    List<Complaint> findByCategoryDepartmentIdAndEscalatedTrueAndAdminWarningIsNotNull(Long departmentId);
    // staff's forwarded escalations for their dept


    long countByCategoryDepartmentId(Long departmentId);
    long countByCategoryDepartmentIdAndStatus(Long departmentId, ComplaintStatus status);

}
