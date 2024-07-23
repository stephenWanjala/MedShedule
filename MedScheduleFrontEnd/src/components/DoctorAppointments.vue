<!-- views/DoctorAppointments.vue -->
<template>
  <div>
    <h1>My Appointments</h1>
    <ul v-if="!appointmentStore.loading">
      <li v-for="appointment in appointmentStore.myAppointments" :key="appointment.id">
        Date: {{ appointment.date }}, Patient: {{ appointment.patientName }}
        <button @click="acceptAppointment(appointment.id)">Accept</button>
        <button @click="completeAppointment(appointment.id)">Complete</button>
      </li>
    </ul>
    <p v-else>Loading appointments...</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAppointmentStore } from '@/stores/AppointmentsStore'

const appointmentStore = useAppointmentStore()

onMounted(() => {
  appointmentStore.fetchMyAppointments()
})

const acceptAppointment = (id: number) => {
  // Implement accept appointment logic
}

const completeAppointment = (id: number) => {
  // Implement complete appointment logic
}
</script>
