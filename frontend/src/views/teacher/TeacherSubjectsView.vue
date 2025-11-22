<script setup>
import { ref, onMounted } from 'vue'
import TeacherNav from '@/components/TeacherNav.vue'
import http from '@/net/index.js'
import { ElMessage } from 'element-plus'

const subjects = ref([])
const loading = ref(false)
const createVisible = ref(false)
const createForm = ref({ title: '' })

const loadSubjects = async () => {
  loading.value = true
  try {
    const data = await http.get('/courses/withCount')
    subjects.value = data.map(c => ({
      id: c.id,
      name: c.title,
      assignmentCount: c.assignmentCount
    }))
  } catch (e) {
    ElMessage.error('获取学科失败')
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  createForm.value = { title: '' }
  createVisible.value = true
}

const submitCreate = async () => {
  try {
    await http.post('/courses/create', { title: createForm.value.title })
    ElMessage.success('创建成功')
    createVisible.value = false
    await loadSubjects()
  } catch (e) {
    ElMessage.error('创建失败')
  }
}

onMounted(() => {
  loadSubjects()
})
</script>

<template>
  <div class="page">
    <TeacherNav />

    <section class="card header-card">
      <div>
        <h2>学科列表</h2>
      </div>
      <el-button type="primary" @click="openCreate">新建学科</el-button>
    </section>

    <section class="card table-card">
      <el-table :data="subjects" stripe border style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="学科名称" min-width="100" />
        <el-table-column prop="assignmentCount" label="已发布作业数量" min-width="160" />
      </el-table>
    </section>

    <el-dialog v-model="createVisible" title="新建学科" width="400px">
      <el-form :model="createForm" label-position="top">
        <el-form-item label="学科名称">
          <el-input v-model="createForm.title" placeholder="请输入学科名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">确认</el-button>
      </template>
    </el-dialog>
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
