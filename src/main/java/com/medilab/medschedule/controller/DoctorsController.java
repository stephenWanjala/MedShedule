package com.medilab.medschedule.controller;

import com.medilab.medschedule.Entity.Role;
import com.medilab.medschedule.Entity.User;
import com.medilab.medschedule.repository.UserRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DoctorsController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/allDoctors")
    public List<User> getDoctors() {
        return userRepository.findByRole(Role.DOCTOR);
    }
}
