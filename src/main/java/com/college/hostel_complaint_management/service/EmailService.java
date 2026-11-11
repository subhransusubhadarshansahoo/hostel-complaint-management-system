package com.college.hostel_complaint_management.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendStaffCredentials(String toEmail, String name, String password) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(toEmail);
        message.setSubject("Your Hostel Complaint Management System Account");
        message.setText(
                "Hello " + name + ",\n\n" +
                        "An account has been created for you on the Hostel Complaint Management System.\n\n" +
                        "Login Email: " + toEmail + "\n" +
                        "Temporary Password: " + password + "\n\n" +
                        "Regards,\nHostel Administration"
        );

        mailSender.send(message);
    }
}