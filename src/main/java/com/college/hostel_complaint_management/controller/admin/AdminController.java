package com.college.hostel_complaint_management.controller.admin;

import com.college.hostel_complaint_management.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DepartmentService departmentService;
    private final HostelService hostelService;
    private final ComplaintCategoryService categoryService;
    private final StaffService staffService;

    private  final ComplaintService complaintService;
    private  final  DashboardService dashboardService;

    public AdminController(DepartmentService departmentService,
                           HostelService hostelService,
                           ComplaintCategoryService categoryService,
                           StaffService staffService,ComplaintService complaintService,DashboardService dashboardService) {
        this.departmentService = departmentService;
        this.hostelService = hostelService;
        this.categoryService = categoryService;
        this.staffService = staffService;
        this.complaintService=complaintService;
        this.dashboardService=dashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        model.addAttribute("departmentCount", departmentService.getAllDepartments().size());
        model.addAttribute("hostelCount", hostelService.getAllHostels().size());
        model.addAttribute("categoryCount", categoryService.getAllCategories().size());
        model.addAttribute("staffCount", staffService.getAllStaff().size());
        model.addAttribute("escalatedCount", complaintService.getUnreviewedEscalatedComplaints().size());
        model.addAttribute("departmentStats", dashboardService.getDepartmentStats());

        return "admin/admin-dashboard";
    }
}