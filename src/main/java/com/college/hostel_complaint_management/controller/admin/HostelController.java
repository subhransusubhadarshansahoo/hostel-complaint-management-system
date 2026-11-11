package com.college.hostel_complaint_management.controller.admin;

import com.college.hostel_complaint_management.entity.Hostel;
import com.college.hostel_complaint_management.service.HostelService;
import com.college.hostel_complaint_management.service.result.HostelResult;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class HostelController {

    private final HostelService hostelService;

    public HostelController(HostelService hostelService) {
        this.hostelService = hostelService;
    }

    @GetMapping("/hostels")
    public String showHostels(Model model) {

        List<Hostel> hostels = hostelService.getAllHostels();
        model.addAttribute("hostels", hostels);

        return "admin/hostel/hostels";
    }

    @GetMapping("/hostels/new")
    public String showHostelForm(Model model) {

        model.addAttribute("hostel", new Hostel());

        return "admin/hostel/hostelForm";
    }

    @PostMapping("/hostels")
    public String addHostel(
            @Valid @ModelAttribute("hostel") Hostel hostel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/hostel/hostelForm";
        }

        HostelResult result = hostelService.saveHostel(hostel);

        if (result == HostelResult.HOSTEL_EXIST) {
            bindingResult.reject("hostelError", "Hostel already exists");
            return "admin/hostel/hostelForm";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "Hostel added successfully");

        return "redirect:/admin/hostels";
    }

    @GetMapping("/hostels/{id}/edit")
    public String editHostel(@PathVariable Long id, Model model) {

        Hostel hostel = hostelService.getHostelById(id);
        model.addAttribute("hostel", hostel);

        return "admin/hostel/hostelForm";
    }

    @PostMapping("/hostels/{id}")
    public String updateHostel(
            @PathVariable Long id,
            @Valid @ModelAttribute("hostel") Hostel hostel,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "admin/hostel/hostelForm";
        }

        HostelResult result = hostelService.updateHostel(id, hostel);

        if (result == HostelResult.HOSTEL_EXIST) {
            bindingResult.reject("hostelError", "Another hostel with this name already exists");
            return "admin/hostel/hostelForm";
        }

        redirectAttributes.addFlashAttribute(
                "successMessage", "Hostel updated successfully");

        return "redirect:/admin/hostels";
    }

    @PostMapping("/hostels/{id}/toggle")
    public String toggleHostel(@PathVariable Long id) {

        hostelService.toggleHostelStatus(id);
        return "redirect:/admin/hostels";
    }
}