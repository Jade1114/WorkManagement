<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import AssignmentTable from '@/components/AssignmentTable.vue'
import Pagination from '@/components/Pagination.vue'
import SearchInput from '@/components/SearchInput.vue'
import http from '@/net/index.js'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'

const assignments = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const searchKeyword = ref('')

const filteredAssignments = computed(() => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (!keyword) return assignments.value
  return assignments.value.filter((a) => {
    const text = `${a.title} ${a.subject || ''}`.toLowerCase()
    return text.includes(keyword)
  })
})

const pagedAssignments = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return filteredAssignments.value.slice(start, start + pageSize.value)
})

const totalAssignments = computed(() => filteredAssignments.value.length)

const loadMySubmissions = async () => {
  loading.value = true
  try {
    const data = await http.get('/education/submissions/my')
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

watch(searchKeyword, () => {
  currentPage.value = 1
})
</script>

<template>
  <div class="page">
    <section class="card header-card">
      <div>
        <h2>已提交作业列表</h2>
      </div>
      <div class="header-actions">
        <SearchInput v-model="searchKeyword" placeholder="搜索标题/学科" style="width: 240px" />
        <el-button :icon="Refresh" :loading="loading" @click="loadMySubmissions">刷新</el-button>
      </div>
    </section>

    <section class="card">
      <AssignmentTable :assignments="pagedAssignments" :loading="loading" :row-key="row => row.submissionId">
        <template #extra-columns>
          <el-table-column prop="submitContent" label="提交内容" min-width="220" />
          <el-table-column prop="comment" label="批改内容" min-width="200">
            <template #default="{ row }">
              {{ row.comment || '未批改' }}
            </template>
          </el-table-column>
        </template>
        <template #status="{ row }">
          <el-tag size="small" :type="row.graded ? 'success' : 'info'">
            {{ row.graded ? '已评分' : '未评分' }}
          </el-tag>
        </template>
        <template #score="{ row }">
          {{ row.graded ? row.score ?? '未评分' : '未评分' }}
        </template>
        <template #footer>
          <Pagination
            :total="totalAssignments"
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
          />
        </template>
      </AssignmentTable>
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

.header-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-m);
}

.muted {
  color: var(--color-text-tertiary);
  margin: 4px 0 0;
}
</style>
