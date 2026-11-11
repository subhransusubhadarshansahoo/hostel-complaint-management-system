package com.college.hostel_complaint_management.service;

import com.college.hostel_complaint_management.entity.ComplaintCategory;
import com.college.hostel_complaint_management.repository.ComplaintCategoryRepository;
import com.college.hostel_complaint_management.repository.DepartmentRepository;
import com.college.hostel_complaint_management.service.result.ComplaintCategoryResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintCategoryService {

    private final ComplaintCategoryRepository complaintCategoryRepository;

    private final DepartmentRepository departmentRepository;

    public ComplaintCategoryService(
            ComplaintCategoryRepository complaintCategoryRepository,DepartmentRepository departmentRepository) {

        this.complaintCategoryRepository = complaintCategoryRepository;
        this.departmentRepository=departmentRepository;
    }

    public List<ComplaintCategory> getAllCategories() {

        return complaintCategoryRepository.findAll();
    }

    public ComplaintCategory getCategoryById(Long id) {

        return complaintCategoryRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Complaint category not found"));
    }

    public ComplaintCategoryResult saveCategory(
            ComplaintCategory category) {

        if (complaintCategoryRepository
                .existsByNameIgnoreCase(category.getName())) {

            return ComplaintCategoryResult.CATEGORY_EXIST;
        }



        Long departmentId = category.getDepartment().getId();

        if (!departmentRepository.existsByIdAndActiveTrue(departmentId)) {
            return ComplaintCategoryResult.DEPARTMENT_INACTIVE;
        }

        category.setActive(true);


        complaintCategoryRepository.save(category);

        return ComplaintCategoryResult.SUCCESS;
    }

    public ComplaintCategoryResult updateCategory(
            Long id,
            ComplaintCategory category) {

        if (complaintCategoryRepository
                .existsByNameIgnoreCaseAndIdNot(
                        category.getName(), id)) {

            return ComplaintCategoryResult.CATEGORY_EXIST;
        }

        ComplaintCategory existingCategory =
                complaintCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Complaint category not found"));

        existingCategory.setName(category.getName());
        existingCategory.setDepartment(category.getDepartment());

        complaintCategoryRepository.save(existingCategory);

        return ComplaintCategoryResult.SUCCESS;
    }

    public void toggleCategoryStatus(Long id) {

        ComplaintCategory category =
                complaintCategoryRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Complaint category not found"));

        category.setActive(!category.isActive());

        complaintCategoryRepository.save(category);
    }

    public List<ComplaintCategory>getActiveCategories() {
       return complaintCategoryRepository.findByActiveTrue();
    }
}