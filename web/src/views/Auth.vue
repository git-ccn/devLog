<script lang="ts" setup>
import { computed, ref } from 'vue'
import Login from '@/views/Login.vue'
import Register from '@/views/Register.vue'
import ForgotPassword from '@/views/ForgotPassword.vue'

type AuthMode = 'login' | 'register' | 'forgot-password'

const currentMode = ref<AuthMode>('login')

const currentComponent = computed(() => {
  if (currentMode.value === 'register') {
    return Register
  }

  if (currentMode.value === 'forgot-password') {
    return ForgotPassword
  }

  return Login
})

const handleSwitch = (mode: AuthMode) => {
  currentMode.value = mode
}
</script>

<template>
  <component :is="currentComponent" @switch="handleSwitch" />
</template>
