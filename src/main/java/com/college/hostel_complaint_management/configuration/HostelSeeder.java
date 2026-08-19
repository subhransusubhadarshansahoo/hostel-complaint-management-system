//package com.college.hostel_complaint_management.configuration;
//
//import com.college.hostel_complaint_management.entity.Hostel;
//import com.college.hostel_complaint_management.repository.HostelRepository;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class HostelSeeder  implements CommandLineRunner {
//
//    private final HostelRepository repository;
//
//    public HostelSeeder(HostelRepository repository) {
//        this.repository = repository;
//    }
//
//    @Override
//    public void run(String... args) throws Exception {
//        addHostel("BOYS-HOSTEL-1");
//        addHostel("BOYS-HOSTEL-2");
//        addHostel("BOYS-HOSTEL-3");
//        addHostel("GIRLS-HOSTEL-1");
//        addHostel("GIRLS-HOSTEL-2");
//        addHostel("GIRLS-HOSTEL-3");
//        addHostel("GIRLS-HOSTEL-1");
//
//
//
//    }
//
//
//    public  void addHostel(String name){
//        if(!repository.existsByName(name)){
//            Hostel hostel=new Hostel();
//            hostel.setName(name);
//            repository.save(hostel);
//        }
//
//    }
//}
