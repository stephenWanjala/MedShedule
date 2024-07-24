import axios from 'axios'

const API_URL = 'http://localhost:8080/api/appointments'

const token = localStorage.getItem('token')

const config = {
  headers: {
    Authorization: `Bearer ${token}`
  }
}

export const bookAppointment = (appointmentRequest: any) => {
  return axios.post(API_URL, appointmentRequest, config)
}

export const getMyAppointments = () => {
  console.log(`config ${JSON.stringify(config)}`)
  return axios.get(`${API_URL}/my`, config)
}

export const rescheduleAppointment = (id: number, appointmentRequest: any) => {
  return axios.put(`${API_URL}/${id}/reschedule`, appointmentRequest, config)
}

export const cancelAppointment = (id: number) => {
  return axios.delete(`${API_URL}/${id}`, config)
}

export const getAvailableSlots = (doctorId: number, date: string) => {
  return axios.get(`${API_URL}/available-slots`, {
    ...config,
    params: { doctorId, date }
  })
}
