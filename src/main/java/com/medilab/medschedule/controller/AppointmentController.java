package com.medilab.medschedule.controller;


import com.medilab.medschedule.Entity.Appointment;
import com.medilab.medschedule.Entity.AppointmentStatus;
import com.medilab.medschedule.Entity.User;
import com.medilab.medschedule.Entity.UserPrincipal;
import com.medilab.medschedule.repository.AppointmentRepository;
import com.medilab.medschedule.repository.UserRepository;
import com.medilab.medschedule.request.AppointmentRequest;
import com.medilab.medschedule.response.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User patient = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getDoctorId()));

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return ResponseEntity.ok(savedAppointment);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getMyAppointments(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User patient = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        List<Appointment> appointments = appointmentRepository.findByPatientAndStatus(patient, AppointmentStatus.SCHEDULED);

        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}/reschedule")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> rescheduleAppointment(@PathVariable Long id, @RequestBody AppointmentRequest request, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (!appointment.getPatient().getId().equals(userPrincipal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only reschedule your own appointments");
        }

        appointment.setAppointmentTime(request.getAppointmentTime());
        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));

        if (!appointment.getPatient().getId().equals(userPrincipal.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can only cancel your own appointments");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/available-slots")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getAvailableSlots(@RequestParam Long doctorId, @RequestParam LocalDate date) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", doctorId));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorAndAppointmentTimeBetween(doctor, startOfDay, endOfDay);

        // Logic to determine available slots based on doctor's schedule and existing appointments
        List<LocalDateTime> availableSlots = calculateAvailableSlots(doctorAppointments, startOfDay, endOfDay);

        return ResponseEntity.ok(availableSlots);
    }

    private List<LocalDateTime> calculateAvailableSlots(List<Appointment> doctorAppointments, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        // Implement logic to calculate available slots
        // This is a simplified example; you might want to consider doctor's working hours, appointment duration, etc.
        List<LocalDateTime> allSlots = new ArrayList<LocalDateTime>();
        LocalDateTime currentSlot = startOfDay;
        while (currentSlot.isBefore(endOfDay)) {
            LocalDateTime finalCurrentSlot = currentSlot;
            if (doctorAppointments.stream().noneMatch(app -> app.getAppointmentTime().equals(finalCurrentSlot))) {
                allSlots.add(currentSlot);
            }
            currentSlot = currentSlot.plusHours(1); // Assuming 1-hour slots
        }
        return allSlots;
    }
}
