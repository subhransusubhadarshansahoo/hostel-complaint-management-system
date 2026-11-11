package com.college.hostel_complaint_management.controller.student.complaint;

import com.college.hostel_complaint_management.dto.ComplaintDto;
import com.college.hostel_complaint_management.entity.Complaint;
import com.college.hostel_complaint_management.repository.UserRepository;
import com.college.hostel_complaint_management.service.ComplaintCategoryService;
import com.college.hostel_complaint_management.service.result.ComplaintResult;
import com.college.hostel_complaint_management.service.ComplaintService;
import com.college.hostel_complaint_management.service.result.ComplaintResult;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class ComplaintController {

    private final ComplaintService complaintService;
    private final ComplaintCategoryService categoryService;
    private final UserRepository userRepository;

    public ComplaintController(
            ComplaintService complaintService,
            ComplaintCategoryService categoryService,
            UserRepository userRepository) {

        this.complaintService = complaintService;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
    }


    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        Long userId = getUserId(userDetails.getUsername());

        List<Complaint> complaints = complaintService.getComplaintsForStudent(userId);

        model.addAttribute("complaints", complaints);

        return "student/student-dashboard";
    }

    @GetMapping("/complaints/new")
    public String showComplaintForm(Model model) {

        model.addAttribute("complaint", new ComplaintDto());
        model.addAttribute("categories", categoryService.getActiveCategories());

        return "student/complaint/complaint-form";
    }

    @PostMapping("/complaints")
    public String createComplaint(
            @Valid @ModelAttribute("complaint") ComplaintDto complaint,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "student/complaint/complaint-form";
        }

        Long userId = getUserId(userDetails.getUsername());

        ComplaintResult result = complaintService.createComplaint(complaint, userId);

        if (result != ComplaintResult.SUCCESS) {
            handleFailure(result, bindingResult);
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "student/complaint/complaint-form";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "Complaint submitted successfully");

        return "redirect:/student/dashboard";
    }

    private void handleFailure(ComplaintResult result, BindingResult bindingResult) {

        switch (result) {

            case STUDENT_NOT_FOUND ->
                    bindingResult.reject("student.notFound", "Student profile not found");

            case CATEGORY_NOT_FOUND ->
                    bindingResult.rejectValue("categoryId", "category.notFound",
                            "Selected category does not exist");

            case CATEGORY_INACTIVE ->
                    bindingResult.rejectValue("categoryId", "category.inactive",
                            "Selected category is no longer active");

            case INVALID_IMAGE_TYPE ->
                    bindingResult.rejectValue("image", "image.invalidType",
                            "Only JPG and PNG images are allowed");

            case IMAGE_TOO_LARGE ->
                    bindingResult.rejectValue("image", "image.tooLarge",
                            "Image must not exceed 5 MB");

            case IMAGE_UPLOAD_FAILED ->
                    bindingResult.reject("image.upload", "Unable to upload image");

            default -> bindingResult.reject("error", "Something went wrong");
        }
    }



    @PostMapping("/complaints/{id}/confirm")
    public String confirm(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Long userId = getUserId(userDetails.getUsername());

        ComplaintResult result = complaintService.confirmResolution(id, userId);

        String message = switch (result) {
            case SUCCESS -> "Complaint marked as resolved. Thank you for confirming.";
            case NOT_YOUR_COMPLAINT -> "You are not allowed to confirm this complaint.";
            case INVALID_STATUS_TRANSITION -> "This complaint is not awaiting confirmation.";
            default -> "Something went wrong.";
        };

        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/student/dashboard";
    }

    @PostMapping("/complaints/{id}/reject")
    public String reject(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Long userId = getUserId(userDetails.getUsername());

        ComplaintResult result = complaintService.rejectResolution(id, userId);

        String message = switch (result) {
            case SUCCESS -> "Sent back to staff for further action.";
            case NOT_YOUR_COMPLAINT -> "You are not allowed to reject this complaint.";
            case INVALID_STATUS_TRANSITION -> "This complaint is not awaiting confirmation.";
            default -> "Something went wrong.";
        };

        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/student/dashboard";
    }

//  -------->Escalation part<----------------

    @PostMapping("/complaints/{id}/escalate")
    public String escalate(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        Long userId = getUserId(userDetails.getUsername());

        ComplaintResult result = complaintService.escalateComplaint(id, userId);

        String message = switch (result) {
            case SUCCESS -> "Reported to admin.";
            case NOT_YOUR_COMPLAINT -> "You are not allowed to report this complaint.";
            case ALREADY_ESCALATED -> "This complaint has already been reported.";
            case NOT_YET_ESCALATABLE -> "This complaint isn't eligible to be reported yet.";
            default -> "Something went wrong.";
        };

        redirectAttributes.addFlashAttribute("successMessage", message);

        return "redirect:/student/dashboard";
    }




    private Long getUserId(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow()
                .getId();
    }
}