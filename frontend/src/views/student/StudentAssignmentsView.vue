<script setup>
import { computed, onMounted, ref } from 'vue'
import StudentAssignmentTable from '@/components/StudentAssignmentTable.vue'
import Pagination from '@/components/Pagination.vue'
import http from '@/net/index.js'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

const assignments = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)

const pagedAssignments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return assignments.value.slice(start, start + pageSize.value)
})

const totalAssignments = computed(() => assignments.value.length)

const loadMySubmissions = async () => {
  loading.value = true
  try {
    const data = await http.get('/submissions/my/list')
    assignments.value = data.map(item => ({
      submissionId: item.submissionId,
      title: item.assignmentTitle,
      subject: item.courseTitle || (item.courseId ? `课程 #${item.courseId}` : '未关联课程'),
      submitContent: item.submitContent,
      comment: item.comment,
      graded: item.graded,
      score: item.score,
    }))
  } catch (e) {
    ElMessage.error('获取提交记录失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadMySubmissions()
})
</script>

<template>
  <div class="page">
    <section class="card header-card">
      <div>
        <h2>已提交作业列表</h2>
      </div>
      <el-button :icon="Refresh" :loading="loading" @click="loadMySubmissions">刷新</el-button>
    </section>

    <section class="card">
      <StudentAssignmentTable :assignments="pagedAssignments" :loading="loading">
        <template #footer>
          <Pagination
            :total="totalAssignments"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
          />
        </template>
      </StudentAssignmentTable>
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
</style>
