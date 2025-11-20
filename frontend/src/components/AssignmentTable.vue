<template>
  <div class="assignment-form card">
    <div class="search-section">
      <el-input v-model="localSearch" placeholder="搜索作业标题或学生姓名..." clearable style="flex: 1">
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
      <el-select v-model="localType" placeholder="全部类型" style="width: 150px">
        <el-option label="全部类型" value="all" />
        <el-option v-for="type in types" :key="type.id" :label="type.name" :value="type.id.toString()" />
      </el-select>
      <el-select v-model="localStudent" placeholder="全部学生" style="width: 150px">
        <el-option label="全部学生" value="all" />
        <el-option v-for="student in students" :key="student.id" :label="student.username" :value="student.id.toString()" />
      </el-select>
      <el-button type="primary" @click="handleSearchClick">搜索</el-button>
    </div>

    <div class="table-container">
      <el-table :data="paginatedAssignments" stripe style="width: 100%" border>
        <el-table-column prop="title" label="作业标题" min-width="200">
          <template #default="scope">
            <span class="article-title">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="student" label="学生" width="120" />
        <el-table-column prop="type" label="作业类型" width="150">
          <template #default="scope">
            <el-tag size="small">{{ scope.row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="150" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag size="small" :type="scope.row.status === '已评分' ? 'success' : 'warning'">
              {{ scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="80">
          <template #default="scope">
            <span v-if="scope.row.score !== null" class="score">{{ scope.row.score }}</span>
            <span v-else class="muted">-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="$emit('grade', scope.row)">评分</el-button>
            <el-button size="small" type="danger" @click="$emit('delete', scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { Search } from '@element-plus/icons-vue'

const props = defineProps({
  assignments: { type: Array, default: () => [] },
  students: { type: Array, default: () => [] },
  types: { type: Array, default: () => [] },
  searchQuery: { type: String, default: '' },
  selectedType: { type: String, default: 'all' },
  selectedStudent: { type: String, default: 'all' },
  currentPage: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 },
})

const emit = defineEmits(['search', 'grade', 'delete'])

const localSearch = ref(props.searchQuery)
const localType = ref(props.selectedType)
const localStudent = ref(props.selectedStudent)

const handleSearchClick = () => {
  emit('search', { searchQuery: localSearch.value, selectedType: localType.value, selectedStudent: localStudent.value })
}

const filteredAssignments = computed(() => {
  return props.assignments.filter((a) => {
    const matchSearch =
      a.title.toLowerCase().includes(localSearch.value.toLowerCase()) ||
      a.student.toLowerCase().includes(localSearch.value.toLowerCase())
    const matchType = localType.value === 'all' || a.typeId.toString() === localType.value
    const matchStudent = localStudent.value === 'all' || a.studentId.toString() === localStudent.value
    return matchSearch && matchType && matchStudent
  })
})

const paginatedAssignments = computed(() => {
  if (!props.currentPage || !props.pageSize) return filteredAssignments.value
  const start = (props.currentPage - 1) * props.pageSize
  const end = start + props.pageSize
  return filteredAssignments.value.slice(start, end)
})

watch(
  () => props.searchQuery,
  (v) => (localSearch.value = v || '')
)
watch(
  () => props.selectedType,
  (v) => (localType.value = v || 'all')
)
watch(
  () => props.selectedStudent,
  (v) => (localStudent.value = v || 'all')
)
</script>

<style scoped>
.assignment-form {
  padding: var(--spacing-m);
}

.search-section {
  display: flex;
  gap: var(--spacing-s);
  padding: var(--spacing-m);
  background-color: var(--color-bg-card);
  border-radius: var(--radius);
  border: 1px solid var(--color-border);
  margin-bottom: var(--spacing-m);
}

.article-title {
  font-weight: 500;
  color: var(--color-primary);
  cursor: pointer;
}

.article-title:hover {
  text-decoration: underline;
}

.table-container {
  background-color: var(--color-bg-card);
  border-radius: var(--radius);
  padding: var(--spacing-s);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow);
}

.score {
  font-weight: 600;
}

.muted {
  color: var(--color-text-tertiary);
}
</style>
