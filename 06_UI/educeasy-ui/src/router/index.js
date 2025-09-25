import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import NotFound from '../views/NotFound.vue'

const PupilProfile = () => import('../views/PupilProfile.vue')

const routes = [
  { path: '/', name: 'home', component: Home, meta: { requiresAuth: true } },
  { path: '/login', name: 'login', component: Login },
  { path: '/register', name: 'register', component: Register },
  { path: '/pupil/:id', name: 'pupil-profile', component: PupilProfile, meta: { requiresAuth: true } },
  { path: '/:pathMatch(.*)*', name: 'not-found', component: NotFound }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    next({ name: 'login', query: { redirect: to.fullPath } })
  } else next()
})

export default router
