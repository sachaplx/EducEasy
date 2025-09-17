import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api, setAuthHeader, clearAuthHeader, decodeJwtRoles } from '../services/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token'))
  const username = ref(localStorage.getItem('username'))
  const roles = ref(JSON.parse(localStorage.getItem('roles') || '[]'))

  const isAuthenticated = computed(() => !!token.value)
  const isDirecteur  = computed(() => roles.value.includes('DIRECTEUR'))
  const isInstituteur = computed(() => roles.value.includes('INSTITUTEUR'))

  if (token.value) setAuthHeader(token.value)

  async function login(creds) {
    const { data } = await api.post('/auth/login', creds)
    token.value = data.token
    username.value = data.username || null

    let parsed = []
    if (Array.isArray(data.roles)) {
      parsed = data.roles.map(r => typeof r === 'string' ? r : r.authority)
    } else if (typeof data.roles === 'string') {
      parsed = data.roles.replace(/[\[\]\s]/g,'').split(',') // tolère "['ROLE_...']"
    } else {
      parsed = decodeJwtRoles(data.token)
    }
    // normalise en DIRECTEUR/INSTITUTEUR
    roles.value = Array.from(new Set(parsed
      .map(r => r.includes('DIRECTEUR') ? 'DIRECTEUR' : (r.includes('INSTITUTEUR') ? 'INSTITUTEUR' : null))
      .filter(Boolean)
    ))

    localStorage.setItem('token', token.value)
    localStorage.setItem('username', username.value || '')
    localStorage.setItem('roles', JSON.stringify(roles.value))
    setAuthHeader(token.value)
  }

  function logout() {
    token.value = null; username.value = null; roles.value = []
    localStorage.removeItem('token'); localStorage.removeItem('username'); localStorage.removeItem('roles')
    clearAuthHeader()
  }

  return { token, username, roles, isAuthenticated, isDirecteur, isInstituteur, login, logout }
})
