<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const adminNav = [
  { label: '主页', path: '/admin/home' },
  { label: '作业列表', path: '/admin/assignments' },
  { label: '学生列表', path: '/admin/students' },
  { label: '学科列表', path: '/admin/subjects' },
]

const studentNav = [
  { label: '主页', path: '/student/home' },
  { label: '作业列表', path: '/student/assignments' },
]

const currentRole = computed(() => {
  if (route.path.startsWith('/admin')) return 'admin'
  if (route.path.startsWith('/student')) return 'student'
  return 'guest'
})

const activePath = computed(() => route.path)

const displayUser = computed(() => {
  if (currentRole.value === 'admin') return { name: 'Admin User', role: '管理员' }
  if (currentRole.value === 'student') return { name: 'Student User', role: '学生' }
  return { name: '访客', role: '未登录' }
})

const avatarLetter = computed(() => displayUser.value.name?.[0]?.toUpperCase() || 'U')

const goAuth = (path) => router.push(path)
</script>

<template>
  <div class="app-shell">
    <header class="app-header card">
      <div class="left">
        <div class="logo">WM</div>
        <div class="title-block">
          <h1>作业管理系统</h1>
          <p class="subtitle">Admin & Student Views</p>
        </div>
        <div class="nav-groups" v-if="currentRole === 'admin'">
          <el-menu mode="horizontal" :default-active="activePath" router class="nav">
            <el-menu-item v-for="item in adminNav" :key="item.path" :index="item.path">
              {{ item.label }}
            </el-menu-item>
          </el-menu>
        </div>
        <div class="nav-groups" v-else-if="currentRole === 'student'">
          <el-menu mode="horizontal" :default-active="activePath" router class="nav">
            <el-menu-item v-for="item in studentNav" :key="item.path" :index="item.path">
              {{ item.label }}
            </el-menu-item>
          </el-menu>
        </div>
      </div>
      <div class="user-block">
        <el-avatar size="large">{{ avatarLetter }}</el-avatar>
        <div>
          <div class="user-name">{{ displayUser.name }}</div>
          <div class="user-role">{{ displayUser.role }}</div>
        </div>
        <template v-if="currentRole === 'guest'">
          <el-button type="primary" @click="goAuth('/login')">登录</el-button>
          <el-button @click="goAuth('/register')">注册</el-button>
        </template>
      </div>
    </header>

    <main class="app-main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: var(--color-bg-page);
  padding: var(--spacing-xl);
}

.app-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-l);
  margin-bottom: var(--spacing-xl);
  gap: var(--spacing-m);
}

.left {
  display: flex;
  align-items: center;
  gap: var(--spacing-m);
  flex: 1;
}

.logo {
  width: 44px;
  height: 44px;
  border-radius: var(--radius);
  background: linear-gradient(135deg, #5fa8ff, #409eff);
  color: #fff;
  font-weight: 800;
  display: grid;
  place-items: center;
}

.title-block h1 {
  margin: 0 0 4px;
}

.subtitle {
  color: var(--color-text-tertiary);
  margin: 0;
}

.nav {
  border-bottom: none;
  background: transparent;
}

.nav-groups {
  display: flex;
  gap: var(--spacing-m);
}

.nav-group {
  display: flex;
  align-items: center;
  gap: var(--spacing-s);
}

.nav-title {
  margin: 0;
  font-size: 13px;
  color: var(--color-text-secondary);
}

.user-block {
  display: flex;
  align-items: center;
  gap: var(--spacing-s);
}

.user-name {
  font-weight: 600;
}

.user-role {
  color: var(--color-text-secondary);
  font-size: 12px;
}

.app-main {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}
</style>
