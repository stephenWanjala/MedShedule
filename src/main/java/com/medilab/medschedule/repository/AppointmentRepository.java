package com.medilab.medschedule.repository;


import com.medilab.medschedule.Entity.Appointment;
import com.medilab.medschedule.Entity.AppointmentStatus;
import com.medilab.medschedule.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientAndStatus(User patient, AppointmentStatus status);
    List<Appointment> findByDoctorAndAppointmentTimeBetween(User doctor, LocalDateTime start, LocalDateTime end);
    List<Appointment> findByDoctorAndStatus(User user, AppointmentStatus appointmentStatus);

    List<Appointment> findByPatient(User patient);

    List<Appointment> findByDoctor(User user);
}
