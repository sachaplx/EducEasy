import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import ConfirmEmail from '../views/ConfirmEmail.vue'
import NotFound from '../views/NotFound.vue'
import Forbidden from '../views/Forbidden.vue'
import ForgotPassword from '../views/ForgotPassword.vue'
import ResetPassword from '../views/ResetPassword.vue'
import AccountView from '../views/AccountView.vue'


const PupilProfile = () => import('../views/PupilProfile.vue')

const routes = [
  { path: '/', name: 'home', component: Home, meta: { requiresAuth: true } },
  { path: '/login', name: 'login', component: Login },
  { path: '/register', name: 'register', component: Register },
  { path: '/pupil/:id', name: 'pupil-profile', component: PupilProfile, meta: { requiresAuth: true } },
  { path: '/confirm', name: 'confirm', component: ConfirmEmail },
  { path: '/:pathMatch(.*)*', name: 'not-found', component: NotFound },
  { path: '/forbidden', name: 'forbidden', component: Forbidden },
  { path: '/forgot-password', name: 'forgot-password', component: ForgotPassword },
  { path: '/reset-password', name: 'reset-password', component: ResetPassword },
  { path: '/account', name: 'account', component: AccountView, meta: { requiresAuth: true } },
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else next()
})

export default router
