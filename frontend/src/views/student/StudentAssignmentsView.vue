<script setup>
import { onMounted, ref } from 'vue'
import StudentAssignmentTable from '@/components/StudentAssignmentTable.vue'
import StudentNav from '@/components/StudentNav.vue'
import http from '@/net/index.js'
import { ElMessage } from 'element-plus'

const assignments = ref([])
const loading = ref(false)

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
    <StudentNav />

    <section class="card header-card">
      <div>
        <h2>已提交作业列表</h2>
      </div>
    </section>

    <section class="card">
      <StudentAssignmentTable :assignments="assignments" v-loading="loading" />
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
