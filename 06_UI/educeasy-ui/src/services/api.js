import axios from 'axios'
import router from '../router'
import { useAuthStore } from '../stores/auth'

export const api = axios.create({
  baseURL: '/',
  timeout: 15000,
})

export function setAuthHeader(token) {
  api.defaults.headers.common['Authorization'] = `Bearer ${token}`
}
export function clearAuthHeader() {
  delete api.defaults.headers.common['Authorization']
}

export function decodeJwt(token) {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const json = atob(base64)
    return JSON.parse(decodeURIComponent(Array.prototype.map.call(json, c =>  '%' + ('00'+c.charCodeAt(0).toString(16)).slice(-2)).join('')))
  } catch { return null }
}

export function decodeJwtRoles(token) {
  const p = decodeJwt(token)
  const roles = p?.roles || p?.authorities || p?.role || []
  if (Array.isArray(roles)) return roles.map(r => (typeof r === 'string' ? r : r.authority))
  if (typeof roles === 'string') return roles.split(/[,\s]+/)
  return []
}

function isJwtExpired(token) {
  const p = decodeJwt(token)
  const now = Math.floor(Date.now() / 1000)
  return !p?.exp || p.exp <= now
}

let redirecting = false;

api.interceptors.request.use((config) => {
  const u = (config.url || '')
  if (u.startsWith('/auth/')) {
    if (config.headers) {
      delete config.headers.Authorization
      delete config.headers.authorization
      return config
    }
  }

  const token = localStorage.getItem('token')
  if (token) {
    if (isJwtExpired(token)) {
      const auth = useAuthStore()
      if (auth.isAuthenticated) auth.logout()
      if (!redirecting && router.currentRoute.value.name !== 'login') {
        redirecting = true
        router.push({ name: 'login', query: { reason: 'expired' } }).finally(() => { redirecting = false })
      }
      return Promise.reject(new axios.Cancel('Token expired'))
    }
    config.headers = config.headers || {}
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    const status = err?.response?.status
    const url = err?.config?.url || ''
    if ((status === 401 || status === 403) && !url.startsWith('/auth/')) {
      const auth = useAuthStore()
      if (auth.isAuthenticated) auth.logout()
      if (!redirecting && router.currentRoute.value.name !== 'login') {
        redirecting = true
        router.push({ name: 'login', query: { reason: 'expired' } }).finally(() => { redirecting = false })
      }
    }
    return Promise.reject(err)
  }
)
