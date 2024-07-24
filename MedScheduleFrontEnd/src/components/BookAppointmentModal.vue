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

          <!-- DateTime Picker -->
          <div v-if="selectedDoctorId">
            <label for="datetime" class="block text-sm font-medium text-gray-700">Select Date and Time</label>
            <DatePicker
              v-model="selectedDateTime"
              :min-date="new Date()"
              :max-date="maxDate"
              :minute-increment="30"
              :available-dates="availableDates"
              :disabled-times="disabledTimes"
              :is24hr="true"
              :masks="{ input: 'YYYY-MM-DD HH:mm' }"
              class="mt-1"
            >
              <template v-slot="{ inputValue, inputEvents }">
                <input
                  class="block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                  :value="inputValue"
                  v-on="inputEvents"
                />
              </template>
            </DatePicker>
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
import { DatePicker } from 'v-calendar'
import { useAuthStore } from '@/stores/AuthStore'
import { useAppointmentStore } from '@/stores/AppointmentsStore'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const appointmentStore = useAppointmentStore()

const selectedDoctorId = ref<number | null>(Number(route.params.doctorId) || null)
const selectedDateTime = ref<Date | null>(null)
const availableDates = ref<Date[]>([])
const disabledTimes = ref<{ dates: Date; hours: number[]; minutes: number[] }[]>([])

const maxDate = computed(() => {
  const date = new Date()
  date.setDate(date.getDate() + 30) // Allow booking up to 30 days in advance
  return date
})

const isFormValid = computed(() => {
  return selectedDoctorId.value && selectedDateTime.value
})

onMounted(async () => {
  if (!authStore?.allDoctors?.length) {
    await authStore.fetchDoctors()
  }
  if (selectedDoctorId.value) {
    await fetchAvailability()
  }
})

watch(selectedDoctorId, async () => {
  if (selectedDoctorId.value) {
    await fetchAvailability()
  }
})

const fetchAvailability = async () => {
  if (selectedDoctorId.value) {
    const now = new Date();
    const isoString = now.toISOString();
    const availability = await appointmentStore.fetchAvailableSlots(selectedDoctorId.value,isoString)
    availableDates.value = availability.availableDates.map(dateStr => new Date(dateStr))
    disabledTimes.value = availability.disabledTimes.map(dt => ({
      dates: new Date(dt.date),
      hours: dt.hours,
      minutes: dt.minutes
    }))
  }
}

const resetDateTime = () => {
  selectedDateTime.value = null
}

const bookAppointment = async () => {
  if (isFormValid.value) {
    try {
      await appointmentStore.createAppointment({
        doctorId: selectedDoctorId.value!,
        appointmentTime: selectedDateTime.value!.toISOString()
      })
      await router.push('/appointments')
    } catch (error) {
      console.error('Failed to book appointment:', error)
      // Handle error (e.g., show error message to user)
    }
  }
}
</script>