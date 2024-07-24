// stores/appointmentStore.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'

import type { Appointment, AppointmentRequest } from '@/types'
import {
  bookAppointment,
  cancelAppointment,
  getAvailableSlots,
  getMyAppointments
} from '@/api/appointments'

export const useAppointmentStore = defineStore('appointment', () => {
  const myAppointments = ref<Appointment[]>([])
  const availableSlots = ref<string[]>([])
  const loading = ref(false)

  const fetchMyAppointments = async () => {
    loading.value = true
    try {
      const response = await getMyAppointments()
      myAppointments.value = response.data
      console.log(`Appointments ${myAppointments.value}`)
      console.log(`Appointments ${response.data}`)
    } catch (error) {
      console.error('Error fetching appointments:', error)
    } finally {
      loading.value = false
    }
  }

  const createAppointment = async (appointmentRequest: AppointmentRequest) => {
    try {
      await bookAppointment(appointmentRequest)
      await fetchMyAppointments()
    } catch (error) {
      console.error('Error booking appointment:', error)
    }
  }

  const cancelAppointmentById = async (id: number) => {
    try {
      await cancelAppointment(id)
      await fetchMyAppointments()
    } catch (error) {
      console.error('Error cancelling appointment:', error)
    }
  }

  const fetchAvailableSlots = async (doctorId: number, date: string) => {
    try {
      const response = await getAvailableSlots(doctorId, date)
      availableSlots.value = response.data
    } catch (error) {
      console.error('Error fetching available slots:', error)
    }
  }

  return {
    myAppointments,
    availableSlots,
    loading,
    fetchMyAppointments,
    createAppointment,
    cancelAppointmentById,
    fetchAvailableSlots
  }
})
