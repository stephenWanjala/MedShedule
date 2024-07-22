<script setup lang="ts">
import { ref } from 'vue';
import router from '@/router';
import { useAuthStore } from '@/stores/AuthStore';

const authStore = useAuthStore();
const username = ref<string>('');
const password = ref<string>('');
const confirmPassword = ref<string>('');
const role = ref<string>('PATIENT');
const message = ref<string>('');
const passwordMismatch = ref<boolean>(false);

const signUp = async (): Promise<void> => {
  if (password.value !== confirmPassword.value) {
    passwordMismatch.value = true;
    message.value = 'Passwords do not match.';
    return;
  } else {
    passwordMismatch.value = false;
  }

  try {
    const response = await authStore.signUp(username.value, password.value, role.value);
    message.value = response.message;
    if (response.success) {
      await router.push('/login');
    }
  } catch (error) {
    message.value = 'Sign up failed';
    console.error('Sign up failed', error);
  }
};
const roleOptions = [
  { value: 'PATIENT', label: 'Patient' },
  { value: 'DOCTOR', label: 'Doctor' }
];

const navigateToSignIn = (): void => {
  router.push('/login');
};
</script>

<template>
  <div class="container mt-5">
    <div class="row justify-content-center">
      <div class="col-md-6">
        <div class="card">
          <div class="card-header">
            <h3 class="text-center">Sign Up</h3>
          </div>
          <div class="card-body">
            <form method="post" @submit.prevent="signUp">
              <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <input type="text" v-model="username" class="form-control" id="username" name="username" required>
              </div>
              <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <input type="password" v-model="password" class="form-control" id="password" name="password" required>
              </div>
              <div class="mb-3">
                <label for="confirmPassword" class="form-label">Confirm Password</label>
                <input type="password" v-model="confirmPassword" class="form-control" id="confirmPassword" name="confirmPassword" required>
              </div>
              <div class="mb-3">
                <label for="role" class="form-label">Role</label>
                <select v-model="role" class="form-select" id="role" name="role" required>
                  <option v-for="option in roleOptions" :key="option.value" :value="option.value">
                    {{ option.label }}
                  </option>
                </select>
              </div>
              <div class="d-grid">
                <button type="submit" class="btn btn-primary">Sign Up</button>
              </div>
              <div v-if="message" :class="{'alert alert-danger mt-3': !passwordMismatch, 'alert alert-warning mt-3': passwordMismatch}" role="alert">
                {{ message }}
              </div>
            </form>
          </div>
          <div class="card-footer text-center">
            <p>Already have an account? <a @click="navigateToSignIn" role="button">Login</a></p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
