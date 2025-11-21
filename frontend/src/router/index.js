import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/welcome/LoginView.vue'),
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/welcome/RegisterView.vue'),
  },
  {
    path: '/teacher/home',
    name: 'teacher-home',
    component: () => import('@/views/teacher/TeacherHomeView.vue'),
  },
  {
    path: '/teacher/assignments',
    name: 'teacher-assignments',
    component: () => import('@/views/teacher/TeacherAssignmentsView.vue'),
  },
  {
    path: '/teacher/students',
    name: 'teacher-students',
    component: () => import('@/views/teacher/TeacherStudentsView.vue'),
  },
  {
    path: '/teacher/subjects',
    name: 'teacher-subjects',
    component: () => import('@/views/teacher/TeacherSubjectsView.vue'),
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
