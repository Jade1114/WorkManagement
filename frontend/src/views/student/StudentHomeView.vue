<template>
  <div class="page">
    <StudentNav />

    <section class="card header-card">
      <div>
        <h2>未提交作业列表</h2>
      </div>
    </section>


    <section class="card">
      <el-table :data="assignments" stripe border style="width: 100%" v-loading="loading" row-key="id">
        <el-table-column prop="title" label="标题" min-width="100" />
        <el-table-column prop="content" label="内容" min-width="300" />
        <el-table-column prop="subject" label="学科/课程" min-width="140" />
        <el-table-column prop="deadline" label="到期时间" min-width="100" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openSubmit(row)">提交</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="submitVisible" title="提交作业" width="520px">
      <p style="margin-bottom: 8px;">{{ currentAssignment?.title }}</p>
      <el-input v-model="submitContent" type="textarea" :rows="6" placeholder="请输入提交内容" />
      <template #footer>
        <el-button @click="submitVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import StudentNav from '@/components/StudentNav.vue'
import http from '@/net/index.js'
import { ElMessage } from 'element-plus'

const assignments = ref([])
const loading = ref(false)
const submitVisible = ref(false)
const submitLoading = ref(false)
const submitContent = ref('')
const currentAssignment = ref(null)

const loadPendingAssignments = async () => {
  loading.value = true
  try {
    const data = await http.get('/assignments/pending')
    assignments.value = data.map(a => ({
      id: a.id,
      title: a.title,
      content: a.content,
      subject: a.courseTitle || (a.courseId ? `课程 #${a.courseId}` : '未关联课程'),
      deadline: a.deadline,
    }))
  } catch (e) {
    ElMessage.error('获取未提交作业失败')
  } finally {
    loading.value = false
  }
}

const openSubmit = (row) => {
  currentAssignment.value = row
  submitContent.value = ''
  submitVisible.value = true
}

const handleSubmit = async () => {
  if (!currentAssignment.value) return
  submitLoading.value = true
  try {
    await http.post('/submissions/submit', {
      assignmentId: currentAssignment.value.id,
      content: submitContent.value,
    })
    ElMessage.success('提交成功')
    submitVisible.value = false
    await loadPendingAssignments()
  } catch (e) {
    ElMessage.error('提交失败')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadPendingAssignments()
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

.quick-actions {
  padding: var(--spacing-l);
}

.actions {
  display: flex;
  gap: var(--spacing-m);
  margin-top: var(--spacing-m);
}
</style>
