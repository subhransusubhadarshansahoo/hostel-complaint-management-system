package com.college.hostel_complaint_management.service;

import com.college.hostel_complaint_management.entity.Department;
import com.college.hostel_complaint_management.repository.DepartmentRepository;
import com.college.hostel_complaint_management.service.result.DepartmentResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    public DepartmentResult saveDepartment(Department department) {
        if(departmentRepository.existsByNameIgnoreCase(department.getName())){
            return DepartmentResult.DEPARTMENT_EXIST;

        }
         departmentRepository.save(department);
        return  DepartmentResult.SUCCESS;
    }

    public void toggleDepartmentStatus(Long id) {
        Department department = getDepartmentById(id);

        department.setActive(!department.isActive());

        departmentRepository.save(department);
    }


    public DepartmentResult updateDepartment(Long id, Department department) {

        if (departmentRepository
                .existsByNameIgnoreCaseAndIdNot(department.getName(), id)) {

            return DepartmentResult.DEPARTMENT_EXIST;
        }

        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        existingDepartment.setName(department.getName());

        departmentRepository.save(existingDepartment);

        return DepartmentResult.SUCCESS;
    }
}