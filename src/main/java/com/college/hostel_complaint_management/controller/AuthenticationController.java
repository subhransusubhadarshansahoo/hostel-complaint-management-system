package com.college.hostel_complaint_management.controller;

import com.college.hostel_complaint_management.dto.UserRegistrationDto;
import com.college.hostel_complaint_management.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {
    // it  contains  mapping for  get/login,get/registration and post/registration

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }


    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") UserRegistrationDto registrationDto,
            RedirectAttributes redirectAttributes) {

      try{
          userService.registerUser(registrationDto);

          redirectAttributes.addFlashAttribute(
                  "success",
                  "Registration successful. Please login."
          );

          return "redirect:/login";

      }
      catch (Exception e){

          redirectAttributes.addFlashAttribute("error",
                  e.getMessage());

          return  "redirect:/login";

      }


    }

}
