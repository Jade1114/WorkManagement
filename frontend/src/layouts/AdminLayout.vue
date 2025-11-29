<template>
  <div class="layout">
    <header class="top-bar card">
      <div class="brand">
        <div class="brand-name">Work Management</div>
      </div>
      <div class="top-actions">
        <el-tag type="info" effect="dark" size="large" class="role-pill">{{ roleDisplay }}</el-tag>
        <el-dropdown>
          <span class="user-entry">
            <el-avatar :size="32">{{ usernameInitial }}</el-avatar>
            <span class="user-name">{{ username || '未登录' }}</span>
            <el-icon><arrow-down /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </header>

    <div class="body">
      <aside class="sidebar card">
        <el-menu router :default-active="activePath" class="menu" background-color="transparent">
          <el-menu-item v-for="item in filteredMenus" :key="item.path" :index="item.path">
            <span>{{ item.label }}</span>
          </el-menu-item>
        </el-menu>
      </aside>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/userStore'
import http from '@/net/index.js'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const menus = [
  { label: '仪表盘', path: '/dashboard', roles: ['admin', 'teacher'] },
  { label: '用户管理', path: '/admin/users', roles: ['admin'] },
  { label: '学科管理', path: '/courses', roles: ['admin', 'teacher'] },
  { label: '作业管理', path: '/assignments/manage', roles: ['admin', 'teacher'] },
  { label: '学生列表', path: '/teacher/students', roles: ['admin', 'teacher'] },
  { label: '学生主页', path: '/student/home', roles: ['student'] },
  { label: '学生作业', path: '/student/assignments', roles: ['student'] },
]

const activePath = computed(() => route.path)
const filteredMenus = computed(() => menus.filter(m => m.roles.includes(userStore.role)))

const username = computed(() => userStore.username)
const usernameInitial = computed(() => (userStore.username ? userStore.username[0].toUpperCase() : 'U'))
const roleDisplay = computed(() => {
  if (userStore.role === 'admin') return 'Admin'
  if (userStore.role === 'teacher') return 'Teacher'
  if (userStore.role === 'student') return 'Student'
  return 'Guest'
})

const goHome = () => {
  if (userStore.role === 'admin' || userStore.role === 'teacher') {
    router.push('/dashboard')
  } else if (userStore.role === 'student') {
    router.push('/student/home')
  } else {
    router.push('/login')
  }
}

const handleLogout = async () => {
  try {
    await http.post('/auth/logout')
  } catch (e) {
    // ignore logout error
  } finally {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-l);
  padding: var(--spacing-l);
  background: var(--color-bg-page);
}

.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-m) var(--spacing-l);
  background: var(--color-bg-glass);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  overflow: hidden;
}

.brand {
  display: flex;
  align-items: center;
  gap: var(--spacing-m);
}

.brand-name {
  font-weight: 700;
  font-size: 16px;
  color: var(--color-text-primary);
}

.top-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-m);
}

.user-entry {
  display: inline-flex;
  align-items: center;
  gap: var(--spacing-s);
  cursor: pointer;
  color: var(--color-text-primary);
  padding: 6px 10px;
  border-radius: var(--radius);
  transition: background 0.15s ease;
}

.role-pill {
  border-radius: 999px;
  padding: 4px 12px;
}

.user-entry:hover {
  background: rgba(61, 126, 255, 0.08);
}

.user-name {
  font-weight: 600;
}

.body {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: var(--spacing-l);
  flex: 1;
  min-height: 0;
  overflow: hidden;
  align-items: stretch;
}

.sidebar {
  padding: var(--spacing-s);
  height: 100%;
  align-self: stretch;
  display: flex;
  flex-direction: column;
  min-height: 0;
  background: var(--color-bg-glass);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow);
  overflow: hidden;
}

.menu {
  border-right: none;
  --el-menu-item-height: 44px;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
}

:deep(.el-menu-item) {
  border-radius: var(--radius);
  margin: 4px 6px;
  color: var(--color-text-secondary);
  font-weight: 500;
  transition: background 0.2s ease, color 0.2s ease;
}

:deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, var(--primary-50), rgba(99, 102, 241, 0.08));
  color: var(--color-primary-strong);
  box-shadow: var(--shadow);
}

:deep(.el-menu-item:hover) {
  background: rgba(99, 102, 241, 0.12);
}

.content {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-l);
  background: var(--color-bg-glass);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 20px;
  padding: var(--spacing-l);
  box-shadow: var(--shadow-strong);
  overflow: auto;
  min-height: 0;
}

@media (max-width: 900px) {
  .body {
    grid-template-columns: 1fr;
  }

  .sidebar {
    position: relative;
    top: 0;
  }
}
</style>
