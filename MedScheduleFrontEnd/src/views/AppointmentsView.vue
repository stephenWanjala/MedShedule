<!-- views/AppointmentsPage.vue -->
<template>
  <Layout>
    <div class="bg-white shadow overflow-hidden sm:rounded-lg">
      <div class="px-4 py-5 sm:px-6">
        <h3 class="text-lg leading-6 font-medium text-gray-900">My Appointments</h3>
      </div>
      <div class="border-t border-gray-200">
        <ul v-if="!appointmentStore.loading" class="divide-y divide-gray-200">
          <li
            v-for="appointment in appointmentStore.myAppointments"
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
                  <span>{{ new Date(appointment.appointmentTime).toLocaleString() }}</span>
                </p>
              </div>
              <div class="ml-2 flex-shrink-0 flex">
                <p
                  :class="[
                    'px-2 inline-flex text-xs leading-5 font-semibold rounded-full',
                    {
                      'bg-green-100 text-green-800': appointment.status === 'SCHEDULED',
                      'bg-red-100 text-red-800': appointment.status === 'CANCELLED',
                      'bg-blue-100 text-blue-800': appointment.status === 'COMPLETED'
                    }
                  ]"
                >
                  {{ appointment.status }}
                </p>
              </div>
            </div>
            <div class="mt-4 flex space-x-2">
              <button
                v-if="appointment.status === 'SCHEDULED'"
                @click="cancelAppointment(appointment.id)"
                class="bg-red-600 text-white px-4 py-2 rounded-md text-sm font-medium hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500"
              >
                Cancel
              </button>
              <!-- Add more action buttons as needed -->
            </div>
          </li>
        </ul>
        <p v-else class="px-4 py-4 sm:px-6 text-gray-500">Loading appointments...</p>
      </div>
    </div>
  </Layout>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

import Layout from '@/components/LayoutComponent.vue'
import { useAppointmentStore } from '@/stores/AppointmentsStore'
import { useAuthStore } from '@/stores/AuthStore'

const appointmentStore = useAppointmentStore()
const authStore = useAuthStore()

onMounted(async () => {
  await appointmentStore.fetchMyAppointments()
})

const cancelAppointment = (id: number) => {
  appointmentStore.cancelAppointmentById(id)
}
</script>
