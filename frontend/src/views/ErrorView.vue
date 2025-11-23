<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()

const homePath = computed(() => {
  if (userStore.role === 'teacher') return '/teacher/home'
  if (userStore.role === 'student') return '/student/home'
  return '/login'
})

const goHome = () => {
  router.push(homePath.value)
}
</script>

<template>
  <div class="page">
    <section class="card error-card">
      <div class="icon">!</div>
      <div class="content">
        <h2>出错了</h2>
        <p>页面访问或请求出现问题，请稍后再试。</p>
        <div class="actions">
          <el-button type="primary" @click="goHome">返回主页</el-button>
          <el-button plain @click="$router.go(-1)">返回上一页</el-button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.page {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.error-card {
  display: grid;
  grid-template-columns: 80px 1fr;
  gap: var(--spacing-m);
  padding: var(--spacing-xl);
  align-items: center;
}

.icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, #f56c6c, #f89c9c);
  color: #fff;
  font-size: 28px;
  font-weight: 700;
}

.content h2 {
  margin: 0 0 var(--spacing-s);
}

.content p {
  margin: 0 0 var(--spacing-m);
  color: var(--color-text-secondary);
}

.actions {
  display: flex;
  gap: var(--spacing-s);
}
</style>
