<script setup lang="ts">
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/AuthStore'
import { onMounted } from 'vue'


const {getUserInfo,user} = useAuthStore()

onMounted(()=>{
  getUserInfo()
})

const route = useRoute()

// Function to determine if a route is active
const isActive = (path: string) => route.path === path
</script>


<template>
  <v-card class="mx-auto">
    <v-layout class="rounded-md">
      <v-navigation-drawer
        expand-on-hover
        rail
      >
        <v-list>
          <v-list-item
            prepend-icon="mdi-account"
            style="font-weight: bold; color: #22c997; font-size: 24px; font-family: Inter, sans-serif;"
            :subtitle="user?.username"
            :title="user?.roles[0]"
          >
<!--            <template v-slot:prepend>-->
<!--              <v-avatar color="#22c997">-->
<!--                <v-icon color="white">mdi-account</v-icon>-->
<!--              </v-avatar>-->
<!--            </template>-->
          </v-list-item>
        </v-list>

        <v-divider></v-divider>
        <v-list density="compact" nav>
          <RouterLink to="/">
          <v-list-item prepend-icon="mdi-home" :class="{ 'active-link': isActive('/') }"  title="Home" value="Home"></v-list-item>

          </RouterLink>
          <v-list-item prepend-icon="mdi-account-multiple" title="Shared with me" value="shared"></v-list-item>
          <v-list-item prepend-icon="mdi-star" title="Starred" value="starred"></v-list-item>
        </v-list>
<!--        <v-list density="compact" nav>-->
<!--          <RouterLink class="nav-link" to="/">-->
<!--            <v-list-item :class="{ 'active-link': isActive('/') }" prepend-icon="mdi-home" title="Home" value="home"></v-list-item>-->
<!--          </RouterLink>-->
<!--        </v-list>-->
      </v-navigation-drawer>

      <v-main class="mx-auto text-center">
        maia
        {{user}}
      </v-main>
    </v-layout>
  </v-card>
</template>

<style scoped>



</style>