package com.college.hostel_complaint_management.service;

import com.college.hostel_complaint_management.entity.Hostel;
import com.college.hostel_complaint_management.repository.HostelRepository;
import com.college.hostel_complaint_management.service.result.HostelResult;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HostelService {

    private final HostelRepository hostelRepository;

    public HostelService(HostelRepository hostelRepository) {
        this.hostelRepository = hostelRepository;
    }

    public List<Hostel> getAllHostels() {
        return hostelRepository.findAll();
    }

    public List<Hostel> getActiveHostels() {
        return hostelRepository.findByActiveTrue();
    }

    public Hostel getHostelById(Long id) {
        return hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));
    }

    public HostelResult saveHostel(Hostel hostel) {

        if (hostelRepository.existsByNameIgnoreCase(hostel.getName())) {
            return HostelResult.HOSTEL_EXIST;
        }

        hostel.setActive(true);
        hostelRepository.save(hostel);

        return HostelResult.SUCCESS;
    }

    public HostelResult updateHostel(Long id, Hostel hostel) {

        if (hostelRepository.existsByNameIgnoreCaseAndIdNot(hostel.getName(), id)) {
            return HostelResult.HOSTEL_EXIST;
        }

        Hostel existingHostel = hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        existingHostel.setName(hostel.getName());
        existingHostel.setType(hostel.getType());

        hostelRepository.save(existingHostel);

        return HostelResult.SUCCESS;
    }

    public void toggleHostelStatus(Long id) {

        Hostel hostel = hostelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hostel not found"));

        hostel.setActive(!hostel.isActive());
        hostelRepository.save(hostel);
    }
}