<!-- views/BookAppointment.vue -->
<template>
  <Layout>
    <div class="max-w-3xl mx-auto bg-white shadow overflow-hidden sm:rounded-lg">
      <div class="px-4 py-5 sm:px-6">
        <h3 class="text-lg leading-6 font-medium text-gray-900">Book an Appointment</h3>
        <p class="mt-1 max-w-2xl text-sm text-gray-500">Please select a doctor and appointment time.</p>
      </div>
      <div class="border-t border-gray-200 px-4 py-5 sm:p-6">
        <form @submit.prevent="bookAppointment" class="space-y-6">
          <!-- Doctor Selection -->
          <div>
            <label for="doctor" class="block text-sm font-medium text-gray-700">Select a Doctor</label>
            <select
              id="doctor"
              v-model="selectedDoctorId"
              class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
              @change="resetDateTime"
            >
              <option value="">Choose a doctor</option>
              <option v-for="doctor in authStore.allDoctors" :key="doctor.id" :value="doctor.id">
                Dr. {{ doctor.username }} - {{  'General Practice' }}
              </option>
            </select>
          </div>

          <!-- Date Selection -->
          <div v-if="selectedDoctorId">
            <label for="date" class="block text-sm font-medium text-gray-700">Select Date</label>
            <input
              type="date"
              id="date"
              v-model="selectedDate"
              :min="minDate"
              :max="maxDate"
              class="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
              @change="fetchAvailableSlots"
            >
          </div>

          <!-- Time Slot Selection -->
          <div v-if="appointmentStore.availableSlots.length > 0">
            <label for="time" class="block text-sm font-medium text-gray-700">Select Time</label>
            <select
              id="time"
              v-model="selectedTime"
              class="mt-1 block w-full pl-3 pr-10 py-2 text-base border-gray-300 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm rounded-md"
            >
              <option value="">Choose a time</option>
              <option v-for="slot in appointmentStore.availableSlots" :key="slot" :value="slot">
                {{ formatTime(slot) }}
              </option>
            </select>
          </div>

          <div v-else-if="selectedDate" class="text-sm text-gray-500">
            No available slots for the selected date. Please choose another date.
          </div>

          <!-- Submit Button -->
          <div>
            <button
              type="submit"
              :disabled="!isFormValid"
              class="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:bg-gray-300 disabled:cursor-not-allowed"
            >
              Book Appointment
            </button>
          </div>
        </form>
      </div>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Layout from '@/components/LayoutComponent.vue'
import { useAuthStore } from '@/stores/AuthStore'
import { useAppointmentStore } from '@/stores/AppointmentsStore'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const appointmentStore = useAppointmentStore()

const selectedDoctorId = ref<number | null>(Number(route.params.doctorId) || null)
const selectedDate = ref('')
const selectedTime = ref('')

const minDate = computed(() => {
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  return tomorrow.toISOString().split('T')[0]
})

const maxDate = computed(() => {
  const maxDate = new Date()
  maxDate.setDate(maxDate.getDate() + 30) // Allow booking up to 30 days in advance
  return maxDate.toISOString().split('T')[0]
})

const isFormValid = computed(() => {
  return selectedDoctorId.value && selectedDate.value && selectedTime.value
})

onMounted(async () => {
  if (!authStore?.allDoctors?.length) {
    await authStore.fetchDoctors()
  }
})

watch(selectedDoctorId, () => {
  selectedDate.value = ''
  selectedTime.value = ''
})

const fetchAvailableSlots = async () => {
  if (selectedDoctorId.value && selectedDate.value) {
    await appointmentStore.fetchAvailableSlots(selectedDoctorId.value, selectedDate.value)
  }
}

const formatTime = (dateTimeString: string) => {
  return new Date(dateTimeString).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
}

const resetDateTime = () => {
  selectedDate.value = ''
  selectedTime.value = ''
}

const bookAppointment = async () => {
  if (isFormValid.value) {
    try {

      const appointmentDateTime = new Date(selectedTime.value); // Correct format
      console.log(`appointmentDateTime: ${JSON.stringify(appointmentDateTime)}`)
      await appointmentStore.createAppointment({
        doctorId: selectedDoctorId.value!,
        appointmentTime: appointmentDateTime.toISOString() // Use ISO format
      })
      await router.push({ name: 'Appointments', query: { success: 'booked' } })
    } catch (error) {
      console.error('Failed to book appointment:', error)
      // Handle error (e.g., show error message to user)
    }
  }
}
</script>
