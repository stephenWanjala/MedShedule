<!-- views/HomePage.vue -->
<template>
  <Layout>
    <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
      <!-- Welcome and Quick Stats -->
      <div class="md:col-span-2 bg-white shadow overflow-hidden sm:rounded-lg">
        <div class="px-4 py-5 sm:px-6">
          <h3 class="text-lg leading-6 font-medium text-gray-900">
            Welcome, {{ authStore.user?.username }}
          </h3>
          <p class="mt-1 max-w-2xl text-sm text-gray-500">Your health dashboard</p>
        </div>
        <div class="border-t border-gray-200 px-4 py-5 sm:p-0">
          <dl class="sm:divide-y sm:divide-gray-200">
            <div class="py-3 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt class="text-sm font-medium text-gray-500">Upcoming appointments</dt>
              <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                {{ upcomingAppointmentsCount }}
              </dd>
            </div>
            <div class="py-3 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt class="text-sm font-medium text-gray-500">Last visit</dt>
              <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                {{ lastVisitDate }}
              </dd>
            </div>
            <div class="py-3 sm:py-5 sm:grid sm:grid-cols-3 sm:gap-4 sm:px-6">
              <dt class="text-sm font-medium text-gray-500">Health status</dt>
              <dd class="mt-1 text-sm text-gray-900 sm:mt-0 sm:col-span-2">
                <span
                  class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800"
                >
                  Good
                </span>
              </dd>
            </div>
          </dl>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="bg-white shadow overflow-hidden sm:rounded-lg">
        <div class="px-4 py-5 sm:px-6">
          <h3 class="text-lg leading-6 font-medium text-gray-900">Quick Actions</h3>
        </div>
        <div class="border-t border-gray-200 px-4 py-5">
          <div class="space-y-3">
            <button
              @click="navigateToBookAppointment"
              class="w-full bg-indigo-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
            >
              Book New Appointment
            </button>
            <button
              @click="navigateToAppointments"
              class="w-full bg-gray-200 text-gray-700 px-4 py-2 rounded-md text-sm font-medium hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
            >
              View All Appointments
            </button>
            <button
              v-if="authStore.user?.role.includes('PATIENT')"
              @click="navigateToMedicalRecords"
              class="w-full bg-gray-200 text-gray-700 px-4 py-2 rounded-md text-sm font-medium hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-gray-500"
            >
              Access Medical Records
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Upcoming Appointments -->
    <div class="mt-8 bg-white shadow overflow-hidden sm:rounded-lg">
      <div class="px-4 py-5 sm:px-6">
        <h3 class="text-lg leading-6 font-medium text-gray-900">Upcoming Appointments</h3>
      </div>
      <div class="border-t border-gray-200">
        <ul v-if="!appointmentStore.loading" class="divide-y divide-gray-200">
          <li
            v-for="appointment in upcomingAppointments"
            :key="appointment.id"
            class="px-4 py-4 sm:px-6"
          >
            <div class="flex items-center justify-between">
              <div>
                <p class="text-sm font-medium text-indigo-600 truncate">
                  {{
                    authStore.user?.role.includes('PATIENT')
                      ? `Dr. ${appointment.doctor.username}`
                      : appointment.patient.username
                  }}
                </p>
                <p class="mt-2 flex items-center text-sm text-gray-500">
                  <CalendarIcon class="flex-shrink-0 mr-1.5 h-5 w-5 text-gray-400" />
                  <span>{{ formatDate(appointment.appointmentTime) }}</span>
                </p>
              </div>
              <div class="ml-2 flex-shrink-0 flex">
                <p
                  class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800"
                >
                  {{ appointment.status }}
                </p>
              </div>
            </div>
          </li>
        </ul>
        <p v-else-if="appointmentStore.loading" class="px-4 py-4 sm:px-6 text-gray-500">
          Loading appointments...
        </p>
        <p v-else class="px-4 py-4 sm:px-6 text-gray-500">No upcoming appointments</p>
      </div>
    </div>

    <!-- Health Tips -->
    <div class="mt-8 bg-white shadow overflow-hidden sm:rounded-lg">
      <div class="px-4 py-5 sm:px-6">
        <h3 class="text-lg leading-6 font-medium text-gray-900">Health Tips</h3>
      </div>
      <div class="border-t border-gray-200 px-4 py-5">
        <ul class="list-disc pl-5 space-y-2">
          <li class="text-sm text-gray-600">
            Stay hydrated: Drink at least 8 glasses of water daily
          </li>
          <li class="text-sm text-gray-600">
            Exercise regularly: Aim for 30 minutes of moderate activity most days
          </li>
          <li class="text-sm text-gray-600">
            Get enough sleep: 7-9 hours per night is recommended for adults
          </li>
          <li class="text-sm text-gray-600">
            Eat a balanced diet: Include plenty of fruits, vegetables, and whole grains
          </li>
        </ul>
      </div>
    </div>

    <!-- Available Doctors (for patients only) -->
    <div v-if="authStore.user?.role.includes('PATIENT')" class="mt-8">
      <h2 class="text-2xl font-semibold text-gray-900">Available Doctors</h2>
      <div class="mt-4 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <div
          v-for="doctor in authStore.allDoctors"
          :key="doctor.id"
          class="bg-white shadow rounded-lg p-6"
        >
          <h3 class="text-lg font-medium text-gray-900">Dr. {{ doctor.username }}</h3>
          <p class="mt-2 text-sm text-gray-500">Specialization: {{ 'General Practice' }}</p>
          <button
            @click="selectDoctor(doctor)"
            class="mt-4 w-full bg-indigo-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Book Appointment
          </button>
        </div>
      </div>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'

import Layout from '@/components/LayoutComponent.vue'
import {CalendarIcon} from '@heroicons/vue/16/solid'
import type { User, Appointment } from '@/types'
import { useAuthStore } from '@/stores/AuthStore'
import { useAppointmentStore } from '@/stores/AppointmentsStore'

const authStore = useAuthStore()
const appointmentStore = useAppointmentStore()
const router = useRouter()

onMounted(() => {
  authStore.fetchDoctors()
  appointmentStore.fetchMyAppointments()
})

const upcomingAppointmentsCount = computed(() => {
  return appointmentStore.myAppointments.filter((a) => new Date(a.appointmentTime) > new Date())
    .length
})

const lastVisitDate = computed(() => {
  const pastAppointments = appointmentStore.myAppointments.filter(
    (a) => new Date(a.appointmentTime) <= new Date()
  )
  if (pastAppointments.length > 0) {
    return formatDate(pastAppointments[pastAppointments.length - 1].appointmentTime)
  }
  return 'No past appointments'
})

const upcomingAppointments = computed(() => {
  return appointmentStore.myAppointments
    .filter((a) => new Date(a.appointmentTime) > new Date())
    .sort((a, b) => new Date(a.appointmentTime).getTime() - new Date(b.appointmentTime).getTime())
    .slice(0, 3) // Show only the next 3 appointments
})

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('en-US', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const selectDoctor = (doctor: User) => {
  router.push({ name: 'BookAppointment', params: { doctorId: doctor.id } })
}

const navigateToBookAppointment = () => {
  router.push({ name: 'BookAppointment' })
}

const navigateToAppointments = () => {
  router.push({ name: 'Appointments' })
}

const navigateToMedicalRecords = () => {
  router.push({ name: 'MedicalRecords' })
}
</script>
