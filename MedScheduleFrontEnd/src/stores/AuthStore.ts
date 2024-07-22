import { defineStore } from 'pinia'
import axios from 'axios'
import { computed, ref } from 'vue'

// src/types.ts
export interface Authority {
  authority: string;
}

export interface LoginResponse {
  role: string;
  id: number;
  authorities: Authority[];
  token: string;
  username: string;
}
export interface User {
  id: number;
  username: string;
  roles: string[];
}
export interface SignupResponse {
  success: boolean;
  message: string;
}


const baseUrl: String = 'http://localhost:8080/api/auth/'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'));
  const user = ref<User | null>(null);

  const isAuthenticated = computed(() => !!token.value);

  const login = async (username: string, password: string): Promise<void> => {
    try {
      const response = await axios.post<LoginResponse>(`${baseUrl}/login`, { username, password });
      token.value = response.data.token;
      localStorage.setItem('token', token.value);
      user.value = {
        id: response.data.id,
        username: response.data.username,
        roles: response.data.authorities.map(auth => auth.authority),
      };
    } catch (error) {
      throw new Error('Failed to login');
    }
  };

  const signUp = async (username: string, password: string, role: string): Promise<SignupResponse> => {
    try {
      const response = await axios.post<SignupResponse>(`${baseUrl}/signup`, { username, password, role });
      return response.data;
    } catch (error) {
      throw new Error('Failed to sign up');
    }
  };


  const logout = (): void => {
    token.value = null;
    user.value = null;
    localStorage.removeItem('token');
  };

  return { token, user, isAuthenticated, login,  logout,signUp };
});