import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useAuthStore } from '@/stores/AuthStore'
import axios from 'axios'

const app = createApp(App)
// Vuetify
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases } from 'vuetify/iconsets/fa'
import { mdi } from 'vuetify/iconsets/mdi'
import { Calendar, DatePicker } from 'v-calendar'
import 'v-calendar/style.css'

const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi
    }
  }
})

app.use(createPinia())
app.use(vuetify)
app.use(router)
// app.use(SetupCalendar, {})
// eslint-disable-next-line vue/multi-word-component-names
app.component('Calendar', Calendar)
app.component('DatePicker', DatePicker)
const authStore = useAuthStore()
axios.interceptors.request.use(
  (config) => {
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      Promise.reject(error).then(() => console.log(error))
    }
  }
)
app.mount('#app')
