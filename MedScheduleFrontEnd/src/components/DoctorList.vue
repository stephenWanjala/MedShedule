<!-- components/DoctorList.vue -->
<template>
  <div>
    <h2>Available Doctors</h2>
    <ul v-if="!authStore.loadingDoctors">
      <li v-for="doctor in authStore.allDoctors" :key="doctor.id">
        {{ doctor.username }}
        <button @click="selectDoctor(doctor)">Book Appointment</button>
      </li>
    </ul>
    <p v-else>Loading doctors...</p>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import type { User } from '@/types'
import { useAuthStore } from '@/stores/AuthStore'
import { useAppointmentStore } from '@/stores/AppointmentsStore'

const authStore = useAuthStore()
const appointmentStore = useAppointmentStore()

onMounted(() => {
  authStore.fetchDoctors()
})

const selectDoctor = (doctor: User) => {
  // Implement logic to open a booking modal or navigate to a booking page
}
</script>
