import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/LoginView.vue'),
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/RegisterView.vue'),
  },
  {
    path: '/admin/home',
    name: 'admin-home',
    component: () => import('@/views/admin/AdminHomeView.vue'),
  },
  {
    path: '/admin/assignments',
    name: 'admin-assignments',
    component: () => import('@/views/admin/AdminAssignmentsView.vue'),
  },
  {
    path: '/admin/students',
    name: 'admin-students',
    component: () => import('@/views/admin/AdminStudentsView.vue'),
  },
  {
    path: '/admin/subjects',
    name: 'admin-subjects',
    component: () => import('@/views/admin/AdminSubjectsView.vue'),
  },
  {
    path: '/student/home',
    name: 'student-home',
    component: () => import('@/views/student/StudentHomeView.vue'),
  },
  {
    path: '/student/assignments',
    name: 'student-assignments',
    component: () => import('@/views/student/StudentAssignmentsView.vue'),
  },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

export default router
