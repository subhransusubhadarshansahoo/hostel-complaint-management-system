package com.college.hostel_complaint_management.entity;

import com.college.hostel_complaint_management.enums.HostelType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "hostels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Hostel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Hostel name is required")
    @Size(max = 100, message = "Hostel name must not exceed 100 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull(message = "Hostel type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HostelType type;

    @Column(nullable = false)
    private boolean active = true;
}