package com.college.hostel_complaint_management.controller.admin;

import com.college.hostel_complaint_management.service.ComplaintService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminComplaintController {


    private  final ComplaintService complaintService;

    public AdminComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }


    @GetMapping("/escalated-complaints")
    public String showEscalatedComplaints(Model model) {
        model.addAttribute("complaints", complaintService.getUnreviewedEscalatedComplaints());
        return "admin/complaint/escalated-complaints";
    }

    @PostMapping("/escalated-complaints/forward")
    public String forwardEscalations(
            @RequestParam("warningMessage") String warningMessage,
            RedirectAttributes redirectAttributes) {

        complaintService.forwardEscalationsToDepartments(warningMessage);

        redirectAttributes.addFlashAttribute("successMessage", "Forwarded to all relevant departments");

        return "redirect:/admin/escalated-complaints";
    }
}
