import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import { useAuthStore } from '@/stores/AuthStore'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/signup',
      name: 'signup',
      component: () => import('../views/SignUpView.vue')
    },
    {
      path: '/appointments',
      name: 'My Appointments',
      component: () => import('../views/AppointmentsView.vue'),
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/BookAppointment',
      name: 'BookAppointment',
      component: () => import('../views/BookAppointmentView.vue'),
      meta: {
        requiresAuth: true
      }
    },

    {
      path: '/doctor-dashboard',
      name: 'Doctor Dashboard',
      component: () => import('../views/DoctorDashboardView.vue'),
      meta: {
        requiresAuth: true
      }
    },
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
      meta: { requiresAuth: true }
    }
  ]
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  if (to.meta.requiresAuth && !authStore.isAuthenticated && !authStore.user) {
    next('/login')
  } else {
    next()
  }
})

export default router
