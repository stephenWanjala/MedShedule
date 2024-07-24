import { defineStore } from 'pinia'
import axios, { AxiosError } from 'axios'
import { computed, ref } from 'vue'
import type { LoginResponse, SignupResponse, User } from '@/types'
import router from '@/router'

const BASE_URL = 'http://localhost:8080/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(null)
  const loadingDoctors = ref(false)
  const allDoctors = ref<User[] | null>(null)

  const isAuthenticated = computed(() => !!token.value)

  const setAuthData = (data: LoginResponse) => {
    token.value = data.token
    localStorage.setItem('token', data.token)
    user.value = {
      id: data.id,
      username: data.username,
      authorities: data.authorities,
      role: data.authorities[0].authority
    }
  }

  const clearAuthData = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  const login = async (username: string, password: string): Promise<void> => {
    try {
      const response = await axios.post<LoginResponse>(`${BASE_URL}/login`, { username, password })
      setAuthData(response.data)
    } catch (error) {
      throw new Error(`Login error: ${error instanceof Error ? error.message : String(error)}`)
    }
  }

  const signUp = async (
    username: string,
    password: string,
    role: string
  ): Promise<SignupResponse> => {
    try {
      const response = await axios.post<SignupResponse>(`${BASE_URL}/signup`, {
        username,
        password,
        role
      })
      return response.data
    } catch (error) {
      throw new Error(`Signup error: ${error instanceof Error ? error.message : String(error)}`)
    }
  }

  const getUserInfo = async (): Promise<LoginResponse | null> => {
    try {
      const response = await axios.get<LoginResponse>(`${BASE_URL}/user`, {
        headers: { Authorization: `Bearer ${token.value}` }
      })
      if (response.data) {
        user.value = {
          id: response.data.id,
          username: response.data.username,
          authorities: response.data.authorities,
          role: response.data.authorities[0].authority
        }
      }
      return response.data
    } catch (error) {
      handleError(error)
      return null
    }
  }

  const fetchDoctors = async (): Promise<void> => {
    try {
      loadingDoctors.value = true
      const response = await axios.get<LoginResponse[]>(`${BASE_URL}/allDoctors`, {
        headers: { Authorization: `Bearer ${token.value}` }
      })
      allDoctors.value = response.data.map((doctor) => ({
        id: doctor.id,
        username: doctor.username,
        role: doctor.authorities[0],
        authorities: doctor.authorities,
        token: doctor.token
      }))
    } catch (error) {
      handleError(error)
    } finally {
      loadingDoctors.value = false
    }
  }

  const handleError = (error: unknown) => {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
      handleUnauthorized()
    } else {
      console.error('API error:', error instanceof Error ? error.message : String(error))
    }
  }

  const handleUnauthorized = () => {
    clearAuthData()
    router.push('/login')
  }

  const logout = (): void => {
    clearAuthData()
  }

  return {
    token,
    user,
    isAuthenticated,
    login,
    logout,
    signUp,
    getUserInfo,
    handleUnauthorized,
    fetchDoctors,
    loadingDoctors,
    allDoctors
  }
})
