import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/stores/AuthStore'
import axios from 'axios'

const app = createApp(App)

app.use(createPinia())
app.use(router)
const authStore = useAuthStore();
axios.interceptors.request.use(
  (config) => {
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);
app.mount('#app')
