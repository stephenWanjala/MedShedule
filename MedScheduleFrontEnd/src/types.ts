// types.ts

export interface Authority {
  authority: string
}

export interface User {
  id: number
  username: string
  role: string
  authorities: Authority[]
}

export enum AppointmentStatus {
  SCHEDULED = 'SCHEDULED',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export interface Appointment {
  id: number
  patient: User
  doctor: User
  appointmentTime: string // Consider using Date if you parse this on the frontend
  status: AppointmentStatus
}

// For creating new appointments
export interface AppointmentRequest {
  doctorId: number
  appointmentTime: string // or Date, depending on how you handle it
}

// Response type for login and user info
export interface LoginResponse {
  id: number
  username: string
  token: string
  authorities: Authority[]
}

// Response type for signup
export interface SignupResponse {
  id: number
  username: string
  role: string
}
