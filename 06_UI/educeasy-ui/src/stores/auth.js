import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api, setAuthHeader, clearAuthHeader, decodeJwtRoles } from '../services/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token'))
  const username = ref(localStorage.getItem('username'))
  const roles = ref(JSON.parse(localStorage.getItem('roles') || '[]'))

  const me = ref(null)
  const meLoading = ref(false)
  const meError = ref(null)

  const isAuthenticated = computed(() => !!token.value)
  const isPrincipal  = computed(() => roles.value.includes('PRINCIPAL'))
  const isTeacher = computed(() => roles.value.includes('TEACHER'))
  const isAdmin = computed(() => roles.value.includes('ADMIN'))

  if (token.value) {
    setAuthHeader(token.value)
  }

  if (token.value && (!roles.value || roles.value.length === 0)) {
    const parsed = decodeJwtRoles(token.value) || [];
    roles.value = Array.from(
      new Set(parsed.map((r) => {
        const s = String(r || "");
        if (s.includes('PRINCIPAL')) return 'PRINCIPAL';
        if (s.includes('TEACHER')) return 'TEACHER';
        if (s.includes('ADMIN')) return 'ADMIN';
        return null;
      }).filter(Boolean)
    )
    );
    localStorage.setItem('roles', JSON.stringify(roles.value))
  }
  
  async function login(creds) {
    const payload = { identifier: creds.identifier ?? creds.username, password: creds.password }
    const { data } = await api.post('/auth/login', payload)
    applyAuthResponse(data)
    await whoAmI()
  }

  async function confirmFromEmail(tokenToConfirm) {
    const { data } = await api.get('/auth/confirm', { params: { token: tokenToConfirm }, })
    applyAuthResponse(data)
    await whoAmI()
  }

  function applyAuthResponse(data) {
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
    const normalized = parsed
      .map((r) => {
        const s = String(r || "");
        if (s.includes('PRINCIPAL')) return 'PRINCIPAL';
        if (s.includes('TEACHER')) return 'TEACHER';
        if (s.includes('ADMIN')) return 'ADMIN';
        return null;
      })
      .filter(Boolean);

    roles.value = Array.from(new Set(normalized))

    localStorage.setItem('token', token.value)
    localStorage.setItem('username', username.value || '')
    localStorage.setItem('roles', JSON.stringify(roles.value))
    setAuthHeader(token.value)
    }

  function logout() {
    token.value = null; username.value = null; roles.value = []
    localStorage.removeItem('token'); localStorage.removeItem('username'); localStorage.removeItem('roles')
    me.value = null;
    meError.value = null;
    clearAuthHeader()
  }

  async function forgotPassword(identifier) {
    const { data } = await api.post('/auth/forgot-password', { identifier })
    return data
  }

  async function resetPassword(tokenReset, newPassword) {
    const { data } = await api.post('/auth/reset-password', { token: tokenReset, newPassword })
    return data
  }

  async function validateResetToken(tokenReset) {
    const { data } = await api.post('/auth/reset-password/validate', { token: tokenReset })
    return data
  }

  async function whoAmI() {
    if (!token.value) {
      me.value = null;
      return null;
    }
    if (meLoading.value) return me.value;

    meLoading.value = true;
    meError.value = null;
    try {
      const { data } = await api.get('/whoami', { params: { _ts: Date.now() } })
      me.value = data;
      return data;
    } catch (e) {
      meError.value = "Impossible de récupérer les informations utilisateur.";
      me.value = null;
      throw e;
    } finally {
      meLoading.value = false;
    }
}

  return { token, username, roles, me, meLoading, meError, isAuthenticated, isPrincipal, isTeacher, isAdmin, login, confirmFromEmail, logout, forgotPassword, resetPassword, validateResetToken, whoAmI }
})
