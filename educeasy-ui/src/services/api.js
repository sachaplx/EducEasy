import axios from 'axios'
import router from '../router'

export const api = axios.create({
  baseURL: '/',
})

export function setAuthHeader(token) {
  api.defaults.headers.common['Authorization'] = `Bearer ${token}`
}
export function clearAuthHeader() {
  delete api.defaults.headers.common['Authorization']
}

api.interceptors.request.use((config) => {
  const u = (config.url || '')
  if (u.startsWith('/auth/')) { // login, register...
    if (config.headers) {
      delete config.headers.Authorization
      delete config.headers.authorization
    }
  }
  return config
})

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response && err.response.status === 401) {
      router.push({ name: 'login', query: { msg: 'expired' } })
    }
    return Promise.reject(err)
  }
)

export function decodeJwtRoles(token) {
  try {
    const base64Url = token.split('.')[1]
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
    const jsonPayload = decodeURIComponent(
      atob(base64).split('').map(c => '%' + ('00'+c.charCodeAt(0).toString(16)).slice(-2)).join('')
    )
    const payload = JSON.parse(jsonPayload)
    const roles = payload.roles
    if (Array.isArray(roles)) return roles.map(r => typeof r === 'string' ? r : r.authority)
    return []
  } catch { return [] }
}
