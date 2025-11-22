<template>
  <section class="card nav-card">
    <div class="nav-content">
      <div>
        <h2>欢迎, {{ username }}</h2>
      </div>
      <el-menu mode="horizontal" :default-active="activePath" router class="nav">
        <el-menu-item v-for="item in teacherNav" :key="item.path" :index="item.path">
          {{ item.label }}
        </el-menu-item>
      </el-menu>
      <el-button type="danger" @click="handleLogout">
        退出登录
      </el-button>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import http from '@/net/index.js'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const teacherNav = [
  { label: '主页', path: '/teacher/home' },
  { label: '作业列表', path: '/teacher/assignments' },
  { label: '学生列表', path: '/teacher/students' },
  { label: '学科列表', path: '/teacher/subjects' },
]

const activePath = computed(() => route.path)
const username = computed(() => userStore.username)

const handleLogout = async () => {
  try {
    await http.post('/auth/logout')
  } catch (e) {
    // 忽略退出接口错误，继续本地清理
  } finally {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.nav-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-m);
  padding: var(--spacing-l);
}

/* logo 固定宽度 */
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

/* 调整内容布局：左边内容不挤压右边菜单 */
.nav-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-m);
  white-space: nowrap;
  /* 禁止换行 */
}

/* 标题区不允许压缩，固定为内容宽度 */
.nav-content>div:first-child {
  flex-shrink: 0;
}

/* 菜单允许扩展，占据剩余空间 */
.nav {
  flex: 1;
  min-width: 400px;
  /* 防止溢出用的关键值，可根据屏幕调 */
  border-bottom: none;
  background: transparent;
}
</style>
