package com.medilab.medschedule.request;


import java.time.LocalDateTime;

public class AppointmentRequest {
    private Long doctorId;
    private LocalDateTime appointmentTime;

    // Getters, setters

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
}