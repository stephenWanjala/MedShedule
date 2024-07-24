<!-- components/BookAppointmentModal.vue -->
<template>
  <div class="modal">
    <h2>Book Appointment with {{ doctor.username }}</h2>
    <input type="date" v-model="selectedDate" @change="fetchAvailableSlots" />
    <select v-model="selectedSlot">
      <option v-for="slot in appointmentStore.availableSlots" :key="slot" :value="slot">
        {{ slot }}
      </option>
    </select>
    <button @click="bookAppointment">Book</button>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { User } from '@/types'
import { useAppointmentStore } from '@/stores/AppointmentsStore'

const props = defineProps<{
  doctor: User
}>()

const appointmentStore = useAppointmentStore()
const selectedDate = ref('')
const selectedSlot = ref('')

const fetchAvailableSlots = () => {
  if (selectedDate.value) {
    appointmentStore.fetchAvailableSlots(props.doctor.id, selectedDate.value)
  }
}

const bookAppointment = () => {
  if (selectedDate.value && selectedSlot.value) {
    appointmentStore.createAppointment({
      doctorId: props.doctor.id,
      appointmentTime: selectedSlot.value
    })
    // Close modal or navigate back
  }
}
</script>
