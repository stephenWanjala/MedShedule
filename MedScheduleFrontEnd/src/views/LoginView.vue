<script setup lang="ts">
import { ref } from 'vue';
import { useAuthStore } from '@/stores/AuthStore';
import router from '@/router'

const authStore = useAuthStore();
const username = ref<string>('');
const password = ref<string>('');
const errorMessage = ref<string | null>(null);

const login = async (): Promise<void> => {
  errorMessage.value = null;
  try {
    await authStore.login(username.value, password.value);
    await router.push("/")
  } catch (error) {
    console.error('Login failed', error);
    errorMessage.value = 'Login failed. Please check your username and password and try again.';
  }
};

const navigateToSignUp =() =>{
  router.push('/signup')
}
</script>

<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">
            <h3 class="text-center">Login</h3>
          </div>
          <div class="card-body">
            <form @submit.prevent="login" method="post">
              <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input type="text" v-model="username" class="form-control" id="username" name="username" required>
              </div>
              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input type="password" v-model="password" class="form-control" id="password" name="password" required>
              </div>
              <div v-if="errorMessage" class="alert alert-danger" role="alert">
                {{ errorMessage }}
              </div>
              <div class="d-grid">
                <button type="submit" class="btn btn-primary">Login</button>
              </div>
            </form>
          </div>
          <div class="card-footer text-center">
            <p>Don't have an account? <a class="btn" @click="navigateToSignUp">Sign up</a></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
