<template>
  <div class="page">
    <TeacherNav />

    <section class="card header-card">
      <div>
        <h2>老师主页</h2>
      </div>
    </section>

    <section class="stats">
      <div
        class="card stat"
        v-for="item in stats"
        :key="item.label"
        @click="go(item.path)"
      >
        <p class="label">{{ item.label }}</p>
        <p class="value">{{ item.value }}</p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import TeacherNav from '@/components/TeacherNav.vue'
import http from '@/net/index.js'

const router = useRouter()

const stats = ref([
  { label: '待评分', value: 0, path: '/teacher/assignments' },
  { label: '已发布作业', value: 0, path: '/teacher/assignments' },
  { label: '学生总数', value: 0, path: '/teacher/students' },
  { label: '学科数量', value: 0, path: '/teacher/subjects' },
])

const loadStats = async () => {
  try {
    const data = await http.get('/teacher/stats')
    stats.value = [
      { label: '待评分', value: data.pendingSubmissions ?? 0, path: '/teacher/assignments' },
      { label: '已发布作业', value: data.assignments ?? 0, path: '/teacher/assignments' },
      { label: '学生总数', value: data.students ?? 0, path: '/teacher/students' },
      { label: '学科数量', value: data.courses ?? 0, path: '/teacher/subjects' },
    ]
  } catch (e) {
    ElMessage.error('获取统计数据失败')
  }
}

const go = (path) => {
  if (path) router.push(path)
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

.header-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: var(--spacing-l);
}

.muted {
  color: var(--color-text-tertiary);
  margin: 4px 0 0;
}

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: var(--spacing-m);
}

.stat {
  padding: var(--spacing-m);
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.15s ease;
}

.stat:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-strong, 0 8px 24px rgba(0,0,0,0.12));
}

.label {
  color: var(--color-text-tertiary);
  margin: 0 0 6px;
}

.value {
  font-size: 22px;
  font-weight: 700;
  margin: 0;
}

.quick-actions {
  padding: var(--spacing-l);
}

.actions {
  display: flex;
  gap: var(--spacing-m);
  margin-top: var(--spacing-m);
}
</style>
