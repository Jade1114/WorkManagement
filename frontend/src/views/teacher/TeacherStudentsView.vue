<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '@/net/index.js'
import { Refresh } from '@element-plus/icons-vue'
import Pagination from '@/components/Pagination.vue'
import TableShell from '@/components/TableShell.vue'

const students = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = 10

const pagedStudents = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return students.value.slice(start, start + pageSize)
})

const loadStudents = async () => {
  loading.value = true
  try {
    const data = await http.get('/user/users?role=student')
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
    <section class="card header-card">
      <div>
        <h2>学生列表</h2>
      </div>
      <el-button :icon="Refresh" :loading="loading" @click="loadStudents">刷新</el-button>
    </section>

    <TableShell :data="pagedStudents" :loading="loading">
      <el-table-column prop="name" label="学生姓名" min-width="160" />
      <template #footer>
        <Pagination
          :total="students.length"
          :page-size="pageSize"
          v-model:current-page="currentPage"
        />
      </template>
    </TableShell>
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
