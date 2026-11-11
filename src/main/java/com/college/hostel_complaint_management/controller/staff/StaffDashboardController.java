package com.college.hostel_complaint_management.controller.staff;

import com.college.hostel_complaint_management.entity.Complaint;
import com.college.hostel_complaint_management.repository.UserRepository;
import com.college.hostel_complaint_management.service.ComplaintService;
import com.college.hostel_complaint_management.service.result.ComplaintResult;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    private final ComplaintService complaintService;
    private final UserRepository userRepository;



    public StaffDashboardController(
            ComplaintService complaintService,
            UserRepository userRepository) {

        this.complaintService = complaintService;
        this.userRepository = userRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        Long userId = getUserId(userDetails.getUsername());

        List<Complaint> complaints =
                complaintService.getDepartmentPendingComplaints(userId);




        model.addAttribute("complaints", complaints);

        return "staff/staff-dashboard";
    }

    private Long getUserId(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"))
                .getId();
    }



    @PostMapping("/complaints/{id}/claim")
    public String claimComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Long userId = getUserId(userDetails.getUsername());

        ComplaintResult result = complaintService.claimComplaint(id, userId);

        if (result == ComplaintResult.ALREADY_CLAIMED) {
            redirectAttributes.addFlashAttribute(
                    "successMessage", "This complaint has already been claimed by someone else");
        } else {
            redirectAttributes.addFlashAttribute(
                    "successMessage", "Complaint claimed successfully");
        }

        return "redirect:/staff/dashboard";
    }

    @GetMapping("/my-complaints")
    public String myComplaints(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        Long userId = getUserId(userDetails.getUsername());

        List<Complaint> complaints = complaintService.getMyClaimedComplaints(userId);

        model.addAttribute("complaints", complaints);

        return "staff/my-complaints";
    }



    @GetMapping("/complaints/{id}/resolve")
    public String showResolveForm(@PathVariable Long id, Model model) {
        model.addAttribute("complaintId", id);
        return "staff/resolve-form";
    }

    @PostMapping("/complaints/{id}/resolve")
    public String resolveComplaint(
            @PathVariable Long id,
            @RequestParam("proofImage") MultipartFile proofImage,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Long userId = getUserId(userDetails.getUsername());

        ComplaintResult result = complaintService.resolveComplaint(id, userId, proofImage);

        String message = switch (result) {
            case SUCCESS -> "Complaint marked as resolved. Waiting for student confirmation.";
            case NOT_YOUR_COMPLAINT -> "You can only resolve complaints assigned to you.";
            case INVALID_STATUS_TRANSITION -> "This complaint cannot be resolved right now.";
            case IMAGE_REQUIRED -> "A proof image is required to resolve a complaint.";
            case INVALID_IMAGE_TYPE -> "Only JPG and PNG images are allowed.";
            case IMAGE_TOO_LARGE -> "Image must not exceed 5 MB.";
            case IMAGE_UPLOAD_FAILED -> "Unable to upload the proof image.";
            default -> "Something went wrong.";
        };

        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/staff/my-complaints";
    }


    @GetMapping("/escalations")
    public String showEscalations(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        Long userId = getUserId(userDetails.getUsername());
        List<Complaint> complaints = complaintService.getDepartmentEscalations(userId);
        model.addAttribute("complaints", complaints);

        return "staff/escalations";
    }
}