package com.college.hostel_complaint_management.service;

import com.college.hostel_complaint_management.dto.ComplaintDto;
import com.college.hostel_complaint_management.entity.Complaint;
import com.college.hostel_complaint_management.entity.ComplaintCategory;
import com.college.hostel_complaint_management.entity.StaffProfile;
import com.college.hostel_complaint_management.entity.StudentProfile;
import com.college.hostel_complaint_management.enums.ComplaintStatus;
import com.college.hostel_complaint_management.exception.ResourceNotFoundException;
import com.college.hostel_complaint_management.repository.ComplaintCategoryRepository;
import com.college.hostel_complaint_management.repository.ComplaintRepository;
import com.college.hostel_complaint_management.repository.StaffProfileRepository;
import com.college.hostel_complaint_management.repository.StudentProfileRepository;
import com.college.hostel_complaint_management.service.result.ComplaintResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintCategoryRepository categoryRepository;
    private final StudentProfileRepository studentProfileRepository;

    private final StaffProfileRepository staffProfileRepository;

    @Value("${app.upload.dir}")
    private String uploadDirectory;
    @Value("${app.resolution.directory}")
    private String resolutionDirectory;


    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(".jpg", ".jpeg", ".png");

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png");

    public ComplaintService(
            ComplaintRepository complaintRepository,
            ComplaintCategoryRepository categoryRepository,
            StudentProfileRepository studentProfileRepository,
            StaffProfileRepository staffProfileRepository) {

        this.complaintRepository = complaintRepository;
        this.categoryRepository = categoryRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.staffProfileRepository = staffProfileRepository;
    }


    public ComplaintResult createComplaint(ComplaintDto dto, Long userId) {

        StudentProfile student = studentProfileRepository
                .findByUserId(userId)
                .orElse(null);

        if (student == null) {
            return ComplaintResult.STUDENT_NOT_FOUND;
        }

        ComplaintCategory category = categoryRepository
                .findById(dto.getCategoryId())
                .orElse(null);

        if (category == null) {
            return ComplaintResult.CATEGORY_NOT_FOUND;
        }


        if (!category.isActive()) {
            return ComplaintResult.CATEGORY_INACTIVE;
        }

        MultipartFile image = dto.getImage();
        String imagePath = null;

        // image is optional — only validate/save if the student actually attached one
        if (image != null && !image.isEmpty()) {

            ComplaintResult validationResult = validateImage(image);

            if (validationResult != ComplaintResult.SUCCESS) {
                return validationResult;
            }

            try {
                imagePath = saveImage(image,uploadDirectory);
            } catch (IOException e) {
                return ComplaintResult.IMAGE_UPLOAD_FAILED;
            }
        }

        Complaint complaint = new Complaint();

        complaint.setDescription(dto.getDescription());
        complaint.setStudent(student);
        complaint.setCategory(category);
        complaint.setAssignedStaff(null);
        complaint.setStatus(ComplaintStatus.PENDING);
        complaint.setImagePath(imagePath);

        LocalDateTime now = LocalDateTime.now();
        complaint.setCreatedAt(now);
        complaint.setUpdatedAt(now);

        complaintRepository.save(complaint);

        return ComplaintResult.SUCCESS;
    }


//    -------> RESOLVE COMPLAINT<--------

    public ComplaintResult resolveComplaint(Long complaintId, Long userId, MultipartFile proofImage) {

        StaffProfile staff = staffProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Staff profile not found"));

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));


        if (complaint.getAssignedStaff() == null
                || !complaint.getAssignedStaff().getId().equals(staff.getId())) {
            return ComplaintResult.NOT_YOUR_COMPLAINT;
        }


        if (complaint.getStatus() != ComplaintStatus.ASSIGNED) {
            return ComplaintResult.INVALID_STATUS_TRANSITION;
        }


        if (proofImage == null || proofImage.isEmpty()) {
            return ComplaintResult.IMAGE_REQUIRED;
        }

        ComplaintResult validationResult = validateImage(proofImage);
        if (validationResult != ComplaintResult.SUCCESS) {
            return validationResult;
        }

        String resolutionImagePath;
        try {
            resolutionImagePath = saveImage(proofImage, resolutionDirectory);
        } catch (IOException e) {
            return ComplaintResult.IMAGE_UPLOAD_FAILED;
        }

        complaint.setResolutionImagePath(resolutionImagePath);
        complaint.setStatus(ComplaintStatus.RESOLUTION_PENDING_CONFIRMATION);
        complaint.setUpdatedAt(LocalDateTime.now());

        complaintRepository.save(complaint);

        return ComplaintResult.SUCCESS;
    }

//----->IMAGE VALIDATION<------
    private ComplaintResult validateImage(MultipartFile image) {

        if (image.getSize() > MAX_FILE_SIZE) {
            return ComplaintResult.IMAGE_TOO_LARGE;
        }

        String originalFileName = image.getOriginalFilename();
        String extension = extractExtension(originalFileName);

        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            return ComplaintResult.INVALID_IMAGE_TYPE;
        }

        String contentType = image.getContentType();

        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            return ComplaintResult.INVALID_IMAGE_TYPE;
        }

        return ComplaintResult.SUCCESS;
    }

    private String extractExtension(String fileName) {

        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf("."));
    }

    private String saveImage(MultipartFile image, String directory) throws IOException {

        Path uploadPath = Paths.get(directory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String extension = extractExtension(image.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;

        Path filePath = uploadPath.resolve(fileName);

        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return directory + fileName;
    }


    public List<Complaint> getComplaintsForStudent(Long userId) {

        StudentProfile student = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        return complaintRepository.findByStudentId(student.getId());
    }




    //get all the pending complaints according to the departments
    public List<Complaint> getDepartmentPendingComplaints(Long userId) {

        StaffProfile staff = staffProfileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Staff profile not found"));

        Long departmentId = staff.getDepartment().getId();

        return complaintRepository.findByCategoryDepartmentIdAndStatus(
                departmentId,
                ComplaintStatus.PENDING
        );
    }






    // complaint claim by the staff
    public ComplaintResult claimComplaint(Long complaintId, Long userId) {

        StaffProfile staff = staffProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found"));

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        if (complaint.getStatus() != ComplaintStatus.PENDING) {
            return ComplaintResult.ALREADY_CLAIMED;
        }

        complaint.setAssignedStaff(staff);
        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaint.setUpdatedAt(LocalDateTime.now());

        try {
            complaintRepository.saveAndFlush(complaint);   // saveAndFlush forces the check NOW, not later
        } catch (ObjectOptimisticLockingFailureException e) {
            return ComplaintResult.ALREADY_CLAIMED;
        }

        return ComplaintResult.SUCCESS;
    }



  /// set the complaint status  from assign to pending state once the staff got disabled by the admin
    public void unassignComplaintsForStaff(Long staffId) {

        List<Complaint> assignedComplaints = complaintRepository.findByAssignedStaffId(staffId);

        for (Complaint complaint : assignedComplaints) {

            if (complaint.getStatus() == ComplaintStatus.ASSIGNED) {
                complaint.setAssignedStaff(null);
                complaint.setStatus(ComplaintStatus.PENDING);
                complaint.setUpdatedAt(LocalDateTime.now());
                complaintRepository.save(complaint);
            }
        }
    }
    public List<Complaint> getMyClaimedComplaints(Long userId) {

        StaffProfile staff = staffProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff profile not found"));

        return complaintRepository.findByAssignedStaffId(staff.getId());
    }





    public ComplaintResult confirmResolution(Long complaintId, Long userId) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        if (!isOwnedByUser(complaint, userId)) {
            return ComplaintResult.NOT_YOUR_COMPLAINT;
        }

        if (complaint.getStatus() != ComplaintStatus.RESOLUTION_PENDING_CONFIRMATION) {
            return ComplaintResult.INVALID_STATUS_TRANSITION;
        }

        complaint.setStatus(ComplaintStatus.RESOLVED);
        complaint.setUpdatedAt(LocalDateTime.now());
        complaintRepository.save(complaint);

        return ComplaintResult.SUCCESS;
    }

    public ComplaintResult rejectResolution(Long complaintId, Long userId) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (!isOwnedByUser(complaint, userId)) {
            return ComplaintResult.NOT_YOUR_COMPLAINT;
        }

        if (complaint.getStatus() != ComplaintStatus.RESOLUTION_PENDING_CONFIRMATION) {
            return ComplaintResult.INVALID_STATUS_TRANSITION;
        }

        complaint.setStatus(ComplaintStatus.ASSIGNED);
        complaint.setUpdatedAt(LocalDateTime.now());
        complaintRepository.save(complaint);

        return ComplaintResult.SUCCESS;
    }

    private boolean isOwnedByUser(Complaint complaint, Long userId) {
        return complaint.getStudent().getUser().getId().equals(userId);
    }










///////////complaint escalation////////////////////////

    public ComplaintResult escalateComplaint(Long complaintId, Long userId) {

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found"));

        if (!isOwnedByUser(complaint, userId)) {
            return ComplaintResult.NOT_YOUR_COMPLAINT;
        }

        if (complaint.isEscalated()) {
            return ComplaintResult.ALREADY_ESCALATED;
        }

        boolean eligible =
                (complaint.getStatus() == ComplaintStatus.PENDING
                        || complaint.getStatus() == ComplaintStatus.ASSIGNED)
                        && complaint.getCreatedAt().isBefore(LocalDateTime.now().minusDays(3));

        if (!eligible) {
            return ComplaintResult.NOT_YET_ESCALATABLE;
        }

        complaint.setEscalated(true);
        complaintRepository.save(complaint);

        return ComplaintResult.SUCCESS;
    }






///complaint  Escalation
    public List<Complaint> getUnreviewedEscalatedComplaints() {
        return complaintRepository.findByEscalatedTrueAndAdminWarningIsNull();
    }

    public ComplaintResult forwardEscalationsToDepartments(String warningMessage) {

        List<Complaint> unreviewed = complaintRepository.findByEscalatedTrueAndAdminWarningIsNull();

        for (Complaint complaint : unreviewed) {
            complaint.setAdminWarning(warningMessage);
            complaintRepository.save(complaint);
        }

        return ComplaintResult.SUCCESS;
    }

    public List<Complaint> getDepartmentEscalations(Long userId) {

        StaffProfile staff = staffProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Staff profile not found"));

        List<Complaint> forwarded = complaintRepository
                .findByCategoryDepartmentIdAndEscalatedTrueAndAdminWarningIsNotNull(staff.getDepartment().getId());

        return forwarded.stream()
                .filter(c -> c.getStatus() == ComplaintStatus.PENDING
                        || c.getStatus() == ComplaintStatus.ASSIGNED)
                .toList();
    }



}