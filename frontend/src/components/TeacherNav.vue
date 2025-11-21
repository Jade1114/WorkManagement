<template>
  <section class="card nav-card">
    <div class="logo">WM</div>
    <div class="nav-content">
      <div>
        <h2>教师端导航</h2>
        <p class="muted">管理作业、学生和学科</p>
      </div>
      <el-menu mode="horizontal" :default-active="activePath" router class="nav">
        <el-menu-item v-for="item in teacherNav" :key="item.path" :index="item.path">
          {{ item.label }}
        </el-menu-item>
      </el-menu>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

const teacherNav = [
  { label: '主页', path: '/teacher/home' },
  { label: '作业列表', path: '/teacher/assignments' },
  { label: '学生列表', path: '/teacher/students' },
  { label: '学科列表', path: '/teacher/subjects' },
]

const activePath = computed(() => route.path)
</script>

<style scoped>
.nav-card {
  display: flex;
  align-items: center;
  gap: var(--spacing-m);
  padding: var(--spacing-l);
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

.nav-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--spacing-m);
  flex: 1;
  flex-wrap: wrap;
}

.nav {
  border-bottom: none;
  background: transparent;
}

.muted {
  color: var(--color-text-tertiary);
  margin: 4px 0 0;
}
</style>
