import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import pinia from '@/stores'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/welcome/LoginView.vue'),
    meta: { guestOnly: true },
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/welcome/RegisterView.vue'),
    meta: { guestOnly: true },
  },
  {
    path: '/teacher/home',
    name: 'teacher-home',
    component: () => import('@/views/teacher/TeacherHomeView.vue'),
    meta: { requiresAuth: true, role: 'teacher' },
  },
  {
    path: '/teacher/assignments',
    name: 'teacher-assignments',
    component: () => import('@/views/teacher/TeacherAssignmentsView.vue'),
    meta: { requiresAuth: true, role: 'teacher' },
  },
  {
    path: '/teacher/students',
    name: 'teacher-students',
    component: () => import('@/views/teacher/TeacherStudentsView.vue'),
    meta: { requiresAuth: true, role: 'teacher' },
  },
  {
    path: '/teacher/subjects',
    name: 'teacher-subjects',
    component: () => import('@/views/teacher/TeacherSubjectsView.vue'),
    meta: { requiresAuth: true, role: 'teacher' },
  },
  {
    path: '/student/home',
    name: 'student-home',
    component: () => import('@/views/student/StudentHomeView.vue'),
    meta: { requiresAuth: true, role: 'student' },
  },
  {
    path: '/student/assignments',
    name: 'student-assignments',
    component: () => import('@/views/student/StudentAssignmentsView.vue'),
    meta: { requiresAuth: true, role: 'student' },
  },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore(pinia)
  const isLoggedIn = !!userStore.token
  const isGuestOnly = to.meta.guestOnly

  if (isGuestOnly && isLoggedIn) {
    const targetHome = userStore.role === 'teacher' ? '/teacher/home' : '/student/home'
    next(targetHome)
    return
  }

  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')
    return
  }

  if (to.meta.role && userStore.role && to.meta.role !== userStore.role) {
    next('/login')
    return
  }

  next()
})

export default router
