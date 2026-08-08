package com.college.hostel_complaint_management.controller;

import com.college.hostel_complaint_management.dto.UserRegistrationDto;
import com.college.hostel_complaint_management.service.RegistrationResult;
import com.college.hostel_complaint_management.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

        UserRegistrationDto dto=new UserRegistrationDto();

        model.addAttribute("user",dto );
        return "register";
    }


    @PostMapping("/register")
    public String registerUser(
           @Valid @ModelAttribute("user") UserRegistrationDto registrationDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if(bindingResult.hasErrors()){
            return  "register";
        }


        if(!registrationDto.getConfirmPassword().equals(registrationDto.getPassword())){
            bindingResult.rejectValue("confirmPassword",null,"password mismatch");
            return "register";

        }
        RegistrationResult result=userService.registerUser(registrationDto);

        if(result==RegistrationResult.EMAIL_EXISTS){
            bindingResult.rejectValue("email",null,"email already exist");
            return "register";
        }

            redirectAttributes.addFlashAttribute("success","user Successfully Register");
            return  "redirect:/login";


    }

}
