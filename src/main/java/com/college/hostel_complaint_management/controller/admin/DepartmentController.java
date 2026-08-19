package com.college.hostel_complaint_management.controller.admin;

import com.college.hostel_complaint_management.entity.Department;
import com.college.hostel_complaint_management.service.DepartmentService;
import com.college.hostel_complaint_management.service.result.DepartmentResult;
import jakarta.validation.Valid;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/departments")
    public String showDepartments(Model model) {

        List<Department> departments = departmentService.getAllDepartments();
        model.addAttribute("departments", departments);

        return "admin/department/departments";
    }


    @GetMapping("/departments/new")
    public String showDepartmentForm(Model model) {

        model.addAttribute("department", new Department());
        return "admin/department/dpForm";
    }


    @PostMapping("/departments")
    public String addDepartment(
            @Valid @ModelAttribute("department") Department department,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/department/dpForm";
        }

        DepartmentResult result = departmentService.saveDepartment(department);

        if (result == DepartmentResult.DEPARTMENT_EXIST) {
            bindingResult.reject(
                    "departmentError",
                    "Department already exists"
            );

            return "admin/department/dpForm";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Department added successfully"
        );

        return "redirect:/admin/departments";
    }




//    <----- mapping for the edit button-->

    @GetMapping("departments/{id}/edit")
    public String edit(@PathVariable Long id,Model model ){
        Department department=departmentService.getDepartmentById(id);
        model.addAttribute("department",department);
        return "admin/department/dpForm";

    }


//   --->mapping for editing the department<-----
    @PostMapping("/departments/{id}")
    public String updateDepartment(
            @PathVariable Long id,
            @Valid @ModelAttribute("department") Department department,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/department/dpForm";
        }

        DepartmentResult result =
                departmentService.updateDepartment(id, department);

        if (result == DepartmentResult.DEPARTMENT_EXIST) {

            bindingResult.reject(
                    "departmentError",
                    "Another department with this name already exists"
            );

            return "admin/department/dpForm";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Department updated successfully"
        );

        return "redirect:/admin/departments";
    }



   @PostMapping("/departments/{id}/toggle")
    public String toggleDepartment(@PathVariable Long id){

     departmentService.toggleDepartmentStatus(id);


     return "redirect:/admin/departments";
   }

}
