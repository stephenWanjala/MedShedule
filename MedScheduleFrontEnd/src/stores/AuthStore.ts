import { defineStore } from 'pinia'
import axios from 'axios'
import { computed, ref } from 'vue'
import type { LoginResponse, SignupResponse, User } from '@/types'
import router from '@/router'

const baseUrl: String = 'http://localhost:8080/api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(null)

  const isAuthenticated = computed(() => !!token.value)

  const login = async (username: string, password: string): Promise<void> => {
    try {
      const response = await axios.post<LoginResponse>(`${baseUrl}/login`, { username, password })
      token.value = response.data.token
      localStorage.setItem('token', token.value)
      user.value = {
        id: response.data.id,
        username: response.data.username,
        roles: response.data.authorities.map((auth) => auth.authority)
      }
    } catch (error) {
      throw new Error(`Error login ${token.value}: ${error}`)
    }
  }

  const handleUnauthorized = () => {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
    router.push('/login') // Redirect to login page
  }

  const signUp = async (
    username: string,
    password: string,
    role: string
  ): Promise<SignupResponse> => {
    try {
      const response = await axios.post<SignupResponse>(`${baseUrl}/signup`, {
        username,
        password,
        role
      })

      return response.data
    } catch (error) {
      throw new Error(`Error signup: ${error}`)
    }
  }

  const getUserInfo = async (): Promise<LoginResponse| null> => {
    try {
      const response = await axios.get<LoginResponse>(`${baseUrl}/user`, {
        headers: {
          'Authorization': `Bearer ${token.value}`
        }
      })
     if (!response.data!=null) {
       user.value = {
         id: response.data.id,
         username: response.data.username,
         roles: response.data.authorities.map((auth) => auth.authority)
       }
     }
      return response.data
    } catch (error) {
      throw new Error(`Error fetching user info: ${error}`)
    }
  }
  const logout = (): void => {
    token.value = null
    user.value = null
    localStorage.removeItem('token')
  }

  return { token, user, isAuthenticated, login, logout, signUp ,getUserInfo, handleUnauthorized}
})
