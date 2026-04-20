<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()

const homePath = computed(() => {
  if (userStore.role === 'admin') return '/dashboard'
  if (userStore.role === 'teacher') return '/dashboard'
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
      <div class="icon">404</div>
      <div class="content">
        <p class="eyebrow">Route Closed</p>
        <h2>这条路径暂时走不通</h2>
        <p>页面访问或请求出现问题，先回到工作台继续处理手上的事。</p>
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
  min-height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.error-card {
  display: grid;
  grid-template-columns: 104px minmax(0, 1fr);
  gap: var(--spacing-l);
  padding: var(--spacing-xl);
  align-items: center;
  max-width: 680px;
  background:
    linear-gradient(135deg, rgba(232, 111, 91, 0.14), transparent 48%),
    var(--color-bg-glass);
}

.icon {
  width: 88px;
  height: 88px;
  border-radius: var(--radius);
  display: grid;
  place-items: center;
  background: linear-gradient(135deg, var(--color-error), var(--color-primary));
  color: #fff;
  font-size: 22px;
  font-weight: 800;
}

.content h2 {
  margin: 0 0 var(--spacing-s);
}

.content p {
  margin: 0 0 var(--spacing-m);
  color: var(--color-text-secondary);
}

.eyebrow {
  color: var(--color-primary-strong);
  font-size: 12px;
  font-weight: 800;
}

.actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-s);
}

@media (max-width: 640px) {
  .error-card {
    grid-template-columns: 1fr;
  }
}
</style>
