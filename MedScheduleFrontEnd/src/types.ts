
// src/types.ts
export interface Authority {
  authority: string
}

export interface LoginResponse {
  role: string
  id: number
  authorities: Authority[]
  token: string
  username: string
}
export interface User {
  id: number
  username: string
  roles: string[]
}
export interface SignupResponse {
  success: boolean
  message: string
}
