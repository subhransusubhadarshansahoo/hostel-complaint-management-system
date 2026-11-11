package com.college.hostel_complaint_management.controller.admin;

import com.college.hostel_complaint_management.entity.ComplaintCategory;
import com.college.hostel_complaint_management.entity.Department;
import com.college.hostel_complaint_management.service.result.ComplaintCategoryResult;
import com.college.hostel_complaint_management.service.ComplaintCategoryService;
import com.college.hostel_complaint_management.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class ComplaintCategoryController {

    private final ComplaintCategoryService complaintCategoryService;
    private final DepartmentService departmentService;

    public ComplaintCategoryController(
            ComplaintCategoryService complaintCategoryService,
            DepartmentService departmentService) {

        this.complaintCategoryService = complaintCategoryService;
        this.departmentService = departmentService;
    }

    @GetMapping("/categories")
    public String showCategories(Model model) {

        List<ComplaintCategory> categories =
                complaintCategoryService.getAllCategories();

        model.addAttribute("categories", categories);

        return "admin/category/categories";
    }

    @GetMapping("/categories/new")
    public String showCategoryForm(Model model) {

        model.addAttribute(
                "category",
                new ComplaintCategory());

        model.addAttribute(
                "departments",
                departmentService.getActiveDepartments());

        return "admin/category/categoryForm";
    }

    @PostMapping("/categories")
    public String addCategory(
            @Valid @ModelAttribute("category")
            ComplaintCategory category,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "departments",
                    departmentService.getActiveDepartments());

            return "admin/category/categoryForm";
        }

        ComplaintCategoryResult result =
                complaintCategoryService.saveCategory(category);

        if (result == ComplaintCategoryResult.CATEGORY_EXIST) {

            bindingResult.reject(
                    "categoryError",
                    "Complaint category already exists");

            model.addAttribute(
                    "departments",
                    departmentService.getActiveDepartments());

            return "admin/category/categoryForm";
        }

        if (result == ComplaintCategoryResult.DEPARTMENT_INACTIVE) {

            bindingResult.reject(
                    "departmentError",
                    "Inactive Department");

            model.addAttribute(
                    "departments",
                    departmentService.getActiveDepartments());

            return "admin/category/categoryForm";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Complaint category added successfully");

        return "redirect:/admin/categories";
    }

    @GetMapping("/categories/{id}/edit")
    public String editCategory(
            @PathVariable Long id,
            Model model) {

        ComplaintCategory category =
                complaintCategoryService.getCategoryById(id);

        model.addAttribute("category", category);

        model.addAttribute(
                "departments",
                departmentService.getActiveDepartments());

        return "admin/category/categoryForm";
    }

    @PostMapping("/categories/{id}")
    public String updateCategory(
            @PathVariable Long id,

            @Valid @ModelAttribute("category")
            ComplaintCategory category,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            model.addAttribute(
                    "departments",
                    departmentService.getActiveDepartments());

            return "admin/category/categoryForm";
        }

        ComplaintCategoryResult result =
                complaintCategoryService.updateCategory(
                        id, category);

        if (result == ComplaintCategoryResult.CATEGORY_EXIST) {

            bindingResult.reject(
                    "categoryError",
                    "Another complaint category with this name already exists");

            model.addAttribute(
                    "departments",
                    departmentService.getActiveDepartments());

            return "admin/category/categoryForm";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Complaint category updated successfully");

        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/toggle")
    public String toggleCategory(
            @PathVariable Long id) {

        complaintCategoryService.toggleCategoryStatus(id);

        return "redirect:/admin/categories";
    }
}