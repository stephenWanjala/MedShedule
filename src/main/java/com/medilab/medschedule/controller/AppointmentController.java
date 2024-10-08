package com.medilab.medschedule.controller;


import com.medilab.medschedule.Entity.Appointment;
import com.medilab.medschedule.Entity.AppointmentStatus;
import com.medilab.medschedule.Entity.User;
import com.medilab.medschedule.Entity.UserPrincipal;
import com.medilab.medschedule.repository.AppointmentRepository;
import com.medilab.medschedule.repository.UserRepository;
import com.medilab.medschedule.request.AppointmentRequest;
import com.medilab.medschedule.response.ResourceNotFoundException;
import com.medilab.medschedule.security.JwtTokenProvider;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private JwtTokenProvider provider;
    @Autowired
    private UserRepository userRepository;

    @PostMapping
//    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> bookAppointment(@RequestBody AppointmentRequest request,@RequestHeader("Authorization") String token) {
            String jwt = token.substring(7);
            String username = provider.getUsernameFromJWT(jwt);
            Optional<User> patient = userRepository.findByUsername(username);
            if (patient.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            };
            Optional<User> doctor = userRepository.findById(request.getDoctorId());
            if (doctor.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Doctor not found");
            }
        Appointment appointment = new Appointment();
        appointment.setPatient(patient.get());
        appointment.setDoctor(doctor.get());
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setStatus(AppointmentStatus.SCHEDULED);

        Appointment savedAppointment = appointmentRepository.save(appointment);

        return ResponseEntity.ok(savedAppointment);
    }

    @GetMapping("/my")
//    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getMyAppointments(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String username = provider.getUsernameFromJWT(jwt);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            if (user.get().getRole().name().equals("DOCTOR")) {
                List<Appointment> appointments = appointmentRepository.findByDoctor(user.get());
                return ResponseEntity.ok(appointments);
            }
            else if (user.get().getRole().name().equals("PATIENT")) {
                List<Appointment> appointments = appointmentRepository.findByPatient(user.get());
                return ResponseEntity.ok(appointments);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");


    }

    @PutMapping("/{id}/reschedule")
//    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> rescheduleAppointment(@PathVariable Long id, @RequestBody AppointmentRequest request, @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String username = provider.getUsernameFromJWT(jwt);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (user.get().getRole().name().equals("DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Doctors cannot reschedule appointments");
        }
        UserPrincipal userPrincipal = UserPrincipal.create(user.get());
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
//    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String jwt = token.substring(7);
        String username = provider.getUsernameFromJWT(jwt);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
        if (user.get().getRole().name().equals("DOCTOR")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Doctors cannot cancel appointments");
        }
        UserPrincipal userPrincipal = UserPrincipal.create(user.get());
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
// @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<?> getAvailableSlots(@RequestParam Long doctorId, @RequestParam LocalDate date) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", doctorId));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorAndAppointmentTimeBetween(doctor, startOfDay, endOfDay);

        // Logic to determine available slots based on doctor's schedule and existing appointments
        List<LocalDateTime> availableSlots = calculateAvailableSlots(doctorAppointments, startOfDay, endOfDay, date);

        return ResponseEntity.ok(availableSlots);
    }

    private List<LocalDateTime> calculateAvailableSlots(List<Appointment> doctorAppointments, LocalDateTime startOfDay, LocalDateTime endOfDay, LocalDate date) {
        // Assuming the doctor's working hours are from 9 AM to 5 PM with 1-hour slots
        LocalTime startWorkingHour = LocalTime.of(9, 0);
        LocalTime endWorkingHour = LocalTime.of(17, 0);

        List<LocalDateTime> allSlots = new ArrayList<LocalDateTime>();
        LocalDateTime currentSlot = startOfDay.with(startWorkingHour);

        // Adjust currentSlot if booking is for today and current time is after startWorkingHour
        if (date.equals(LocalDate.now()) && LocalDateTime.now().isAfter(currentSlot)) {
            currentSlot = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            if (currentSlot.getHour() >= endWorkingHour.getHour()) {
                return allSlots; // No slots available if current time is after endWorkingHour
            }
        }

        while (currentSlot.toLocalTime().isBefore(endWorkingHour) && currentSlot.isBefore(endOfDay)) {
            LocalDateTime finalCurrentSlot = currentSlot;
            if (doctorAppointments.stream().noneMatch(app -> app.getAppointmentTime().equals(finalCurrentSlot))) {
                allSlots.add(currentSlot);
            }
            currentSlot = currentSlot.plusHours(1); // Assuming 1-hour slots
        }
        return allSlots;
    }





}
