import { defineStore } from 'pinia'
import { ref } from 'vue'
import { api } from '../services/api'

export const useProfileStore = defineStore('profile', () => {
  const profile = ref(null)

  async function loadProfile() {
    const { data } = await api.get('/auth/me')
    profile.value = data
    return data
  }

  async function updateEmail(newEmail, currentPassword) {
    const { data } = await api.put('/auth/me/email', { email: newEmail, currentPassword })
    if (profile.value) {
      profile.value.email = data.email
    }
    return data
  }

  async function changePassword(currentPassword, newPassword) {
    const { data } = await api.put('/auth/me/password', {
      currentPassword,
      newPassword,
    })
    return data
  }

  return {
    profile,
    loadProfile,
    updateEmail,
    changePassword,
  }
})
