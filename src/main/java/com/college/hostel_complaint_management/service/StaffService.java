package com.college.hostel_complaint_management.service;

import com.college.hostel_complaint_management.dto.StaffFormDto;
import com.college.hostel_complaint_management.entity.Department;
import com.college.hostel_complaint_management.entity.Role;
import com.college.hostel_complaint_management.entity.StaffProfile;
import com.college.hostel_complaint_management.entity.User;
import com.college.hostel_complaint_management.repository.DepartmentRepository;
import com.college.hostel_complaint_management.repository.RoleRepository;
import com.college.hostel_complaint_management.repository.StaffProfileRepository;
import com.college.hostel_complaint_management.repository.UserRepository;
import com.college.hostel_complaint_management.service.result.StaffResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffService {

    private final UserRepository userRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final DepartmentRepository departmentRepository;

    private final ComplaintService complaintService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(StaffService.class);

    public StaffService(UserRepository userRepository,
                        StaffProfileRepository staffProfileRepository,
                        DepartmentRepository departmentRepository,
                        ComplaintService complaintService,
                        RoleRepository roleRepository,
                        PasswordEncoder passwordEncoder,
                        EmailService emailService) {
        this.userRepository = userRepository;
        this.staffProfileRepository = staffProfileRepository;
        this.departmentRepository = departmentRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
         this.emailService=emailService;
         this.complaintService=complaintService;
    }

    public List<StaffProfile> getAllStaff() {
        return staffProfileRepository.findAll();
    }

    public StaffProfile getStaffById(Long id) {
        return staffProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff member not found"));
    }

    @Transactional
    public StaffResult registerStaff(StaffFormDto dto) {

        if (userRepository.existsByEmailIgnoreCase(dto.getEmail())) {
            return StaffResult.EMAIL_EXIST;
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            return StaffResult.PASSWORD_REQUIRED;
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            return StaffResult.PASSWORD_MISMATCH;
        }

        if (!departmentRepository.existsByIdAndActiveTrue(dto.getDepartmentId())) {
            return StaffResult.DEPARTMENT_INACTIVE;
        }

        Role staffRole = roleRepository.findByName("STAFF")
                .orElseThrow(() -> new RuntimeException("STAFF role not configured"));

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(staffRole);
        user.setEnabled(true);

        userRepository.save(user);

        StaffProfile profile = new StaffProfile();
        profile.setUser(user);
        profile.setDepartment(department);

        staffProfileRepository.save(profile);

        staffProfileRepository.save(profile);

        try {
            emailService.sendStaffCredentials(dto.getEmail(), dto.getName(), dto.getPassword());
        } catch (Exception e) {
            logger.error("Failed to send credentials email to {}: {}", dto.getEmail(), e.getMessage());
            return StaffResult.SUCCESS_EMAIL_FAILED;
        }

        return StaffResult.SUCCESS;


    }

    @Transactional
    public StaffResult updateStaff(Long id, StaffFormDto dto) {

        StaffProfile existing = staffProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff member not found"));

        User user = existing.getUser();

        if (userRepository.existsByEmailIgnoreCaseAndIdNot(dto.getEmail(), user.getId())) {
            return StaffResult.EMAIL_EXIST;
        }

        if (!departmentRepository.existsByIdAndActiveTrue(dto.getDepartmentId())) {
            return StaffResult.DEPARTMENT_INACTIVE;
        }

        // password is optional on edit — only touch it if admin typed a new one
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (!dto.getPassword().equals(dto.getConfirmPassword())) {
                return StaffResult.PASSWORD_MISMATCH;
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());

        userRepository.save(user);

        existing.setDepartment(department);
        staffProfileRepository.save(existing);

        return StaffResult.SUCCESS;
    }

    public void toggleStaffStatus(Long id) {

        StaffProfile profile = staffProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff member not found"));

        User user = profile.getUser();
        boolean disabling = user.getEnabled();

        user.setEnabled(!user.getEnabled());
        userRepository.save(user);

        if (disabling) {
            complaintService.unassignComplaintsForStaff(profile.getId());
        }
    }
}