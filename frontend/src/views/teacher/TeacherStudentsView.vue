<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import TeacherNav from '@/components/TeacherNav.vue'
import http from '@/net/index.js'

const students = ref([])
const loading = ref(false)

const loadStudents = async () => {
  loading.value = true
  try {
    const data = await http.get('/users/students')
    students.value = data
      .filter((u) => u.role === 'student')
      .map((u) => ({
        id: u.id,
        name: u.username
      }))
  } catch (e) {
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStudents()
})
</script>

<template>
  <div class="page">
    <TeacherNav />

    <section class="card header-card">
      <div>
        <h2>学生列表</h2>
      </div>
    </section>

    <section class="card table-card">
      <el-table :data="students" stripe border style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="学生姓名" min-width="160" />
      </el-table>
    </section>
  </div>
</template>

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

.table-card {
  padding: var(--spacing-m);
}
</style>
