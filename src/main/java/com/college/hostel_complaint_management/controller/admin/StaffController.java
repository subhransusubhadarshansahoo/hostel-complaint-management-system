package com.college.hostel_complaint_management.controller.admin;

import com.college.hostel_complaint_management.dto.StaffFormDto;
import com.college.hostel_complaint_management.entity.StaffProfile;
import com.college.hostel_complaint_management.service.DepartmentService;
import com.college.hostel_complaint_management.service.StaffService;
import com.college.hostel_complaint_management.service.result.StaffResult;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class StaffController {

    private final StaffService staffService;
    private final DepartmentService departmentService;

    public StaffController(StaffService staffService, DepartmentService departmentService) {
        this.staffService = staffService;
        this.departmentService = departmentService;
    }

    @GetMapping("/staff")
    public String showStaff(Model model) {

        List<StaffProfile> staffList = staffService.getAllStaff();
        model.addAttribute("staffList", staffList);

        return "admin/staff/staffs";
    }

    @GetMapping("/staff/new")
    public String showStaffForm(Model model) {

        model.addAttribute("staffForm", new StaffFormDto());
        model.addAttribute("departments", departmentService.getActiveDepartments());

        return "admin/staff/staffForm";
    }

    @PostMapping("/staff")
    public String addStaff(
            @Valid @ModelAttribute("staffForm") StaffFormDto staffForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getActiveDepartments());
            return "admin/staff/staffForm";
        }

        StaffResult result = staffService.registerStaff(staffForm);

        if (result == StaffResult.SUCCESS_EMAIL_FAILED) {
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Staff member added successfully, but the credentials email could not be sent. Please share login details manually."
            );
            return "redirect:/admin/staff";
        }

        if (result != StaffResult.SUCCESS) {
            bindingResult.reject("staffError", messageFor(result));
            model.addAttribute("departments", departmentService.getActiveDepartments());
            return "admin/staff/staffForm";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Staff member added successfully");
        return "redirect:/admin/staff";
    }

    @GetMapping("/staff/{id}/edit")
    public String editStaff(@PathVariable Long id, Model model) {

        StaffProfile profile = staffService.getStaffById(id);

        StaffFormDto dto = new StaffFormDto();
        dto.setId(profile.getId());
        dto.setName(profile.getUser().getName());
        dto.setEmail(profile.getUser().getEmail());
        dto.setPhone(profile.getUser().getPhone());
        dto.setDepartmentId(profile.getDepartment().getId());
        // password left blank on purpose

        model.addAttribute("staffForm", dto);
        model.addAttribute("departments", departmentService.getAllDepartments());

        return "admin/staff/staffForm";
    }

    @PostMapping("/staff/{id}")
    public String updateStaff(
            @PathVariable Long id,
            @Valid @ModelAttribute("staffForm") StaffFormDto staffForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "admin/staff/staffForm";
        }

        StaffResult result = staffService.updateStaff(id, staffForm);

        if (result != StaffResult.SUCCESS) {
            bindingResult.reject("staffError", messageFor(result));
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "admin/staff/staffForm";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Staff member updated successfully");
        return "redirect:/admin/staff";
    }

    @PostMapping("/staff/{id}/toggle")
    public String toggleStaff(@PathVariable Long id) {
        staffService.toggleStaffStatus(id);
        return "redirect:/admin/staff";
    }

    private String messageFor(StaffResult result) {
        return switch (result) {
            case EMAIL_EXIST -> "A staff member with this email already exists";
            case PASSWORD_REQUIRED -> "Password is required";
            case PASSWORD_MISMATCH -> "Password and confirm password do not match";
            case DEPARTMENT_INACTIVE -> "Selected department is not active";
            default -> "Something went wrong";
        };
    }
}