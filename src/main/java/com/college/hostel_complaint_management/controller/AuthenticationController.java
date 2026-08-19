package com.college.hostel_complaint_management.controller;

import com.college.hostel_complaint_management.dto.UserRegistrationDto;
import com.college.hostel_complaint_management.entity.Hostel;
import com.college.hostel_complaint_management.repository.HostelRepository;
import com.college.hostel_complaint_management.service.result.RegistrationResult;
import com.college.hostel_complaint_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AuthenticationController {
    // it  contains  mapping for  get/login,get/registration and post/registration

    private final UserService userService;

    private final HostelRepository hostelRepository;

    public AuthenticationController(UserService userService, HostelRepository hostelRepository) {
        this.userService = userService;
        this.hostelRepository = hostelRepository;
    }


    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    ////--------->IT MAP THE USER TO THE LOGIN PAGE<--------
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    ///----> IT SHOW THE USER REGISTRATION FORM<------------
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {

        UserRegistrationDto dto = new UserRegistrationDto();



        model.addAttribute("user", dto);

        model.addAttribute("hostels", getHostelInfo());
        return "register";
    }

    //   ------->THIS CONTROLLER HANDLING THE USER REGISTRATION <--------
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("hostels",getHostelInfo());
            return "register";
        }


        if (!registrationDto.getConfirmPassword().equals(registrationDto.getPassword())) {
            bindingResult.rejectValue("confirmPassword", null, "password mismatch");
            model.addAttribute("hostels",getHostelInfo());
            return "register";

        }
        RegistrationResult result = userService.registerUser(registrationDto);

        if (result == RegistrationResult.EMAIL_EXISTS) {
            bindingResult.rejectValue("email", null, "email already exist");
            model.addAttribute("hostels",getHostelInfo());
            return "register";
        }

        redirectAttributes.addFlashAttribute("success", "user Successfully Register");
        return "redirect:/login";


    }


    @GetMapping("/welcome")
    public String welcome() {
        return "welcome";
    }


    public List<Hostel> getHostelInfo() {
        return hostelRepository.findAll();


    }

}
