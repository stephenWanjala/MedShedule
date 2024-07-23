<!-- components/AppointmentList.vue -->
<template>
  <div>
    <h2>My Appointments</h2>
    <ul v-if="!appointmentStore.loading">
      <li v-for="appointment in appointmentStore.myAppointments" :key="appointment.id">
        Date: {{ appointment.date }}, Doctor: {{ appointment.doctorName }}
        <button @click="cancelAppointment(appointment.id)">Cancel</button>
      </li>
    </ul>
    <p v-else>Loading appointments...</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAppointmentStore } from '@/stores/AppointmentsStore'
import { useAuthStore } from '@/stores/AuthStore'

const appointmentStore = useAppointmentStore()
const authStore = useAuthStore()

onMounted(() => {
  appointmentStore.fetchMyAppointments()
})

const cancelAppointment = (id: number) => {
  appointmentStore.cancelAppointmentById(id)
}
</script>
