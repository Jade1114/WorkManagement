import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// ============ 1️⃣ 懒加载导入所有页面组件 ============
// 只在需要时加载，可以减小首屏加载时间

// 认证页面
const LoginView = () => import('@/views/auth/LoginView.vue')
const RegisterView = () => import('@/views/auth/RegisterView.vue')

// ============ 2️⃣ 定义所有路由 ============
const routes = [
  // 根路径重定向到登录
  { path: '/', redirect: '/login' },

  // ===== 认证路由（不需要登录就能访问）=====
  {
    path: '/login',
    component: LoginView,
    meta: { requiresAuth: false, title: '登录' },
  },
  {
    path: '/register',
    component: RegisterView,
    meta: { requiresAuth: false, title: '注册' },
  },



  // 捕获所有未定义的路由
  { path: '/:pathMatch(.*)*', redirect: '/login' },
]

// ============ 3️⃣ 创建 router 实例 ============
const router = createRouter({
  // createWebHistory() 使用 HTML5 History API
  // 不会在 URL 中显示 #，更美观
  history: createWebHistory(),
  routes,
})

// ============ 4️⃣ 路由守卫 - 检查权限 ============
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  // 从 meta 中获取这个路由是否需要登录
  const requiresAuth = to.meta.requiresAuth

  // 从 meta 中获取这个路由需要什么角色
  const requiredRoles = to.meta.roles

  // ===== 情况 1：这个页面需要登录 =====
  if (requiresAuth) {
    // 检查是否已登录
    if (!authStore.isAuthenticated) {
      // 没登录 → 跳转到登录页
      next('/login')
      return
    }

    // ===== 情况 2：这个页面需要特定角色 =====
    if (requiredRoles && !requiredRoles.includes(authStore.user.role)) {
      // 比如学生访问 /teacher/courses，就会被拒绝
      next('/login')
      return
    }
  }

  // 通过检查 → 允许进入这个页面
  next()
})

export default router