<script setup>
import { ref, computed } from 'vue'
import TeacherAssignmentTable from '@/components/TeacherAssignmentTable.vue'
import Pagination from '@/components/Pagination.vue'
import TeacherNav from '@/components/TeacherNav.vue'

const assignments = ref([
  { id: 1, title: '数据结构链表', subject: '数据结构', student: 'Alice', submitTime: '2024-12-01', status: '已评分', score: 90 },
  { id: 2, title: '操作系统调度算法', subject: '操作系统', student: 'Bob', submitTime: '2024-12-02', status: '待评分', score: null },
  { id: 3, title: '计算机网络实验', subject: '计算机网络', student: 'Charlie', submitTime: '2024-12-03', status: '待评分', score: null },
  { id: 4, title: '数据库索引', subject: '数据库', student: 'Alice', submitTime: '2024-12-04', status: '已评分', score: 95 },
  { id: 5, title: '前端组件化', subject: '前端开发', student: 'Bob', submitTime: '2024-12-05', status: '已评分', score: 88 },
])

const currentPage = ref(1)
const pageSize = ref(5)
const total = computed(() => assignments.value.length)
</script>

<template>
  <div class="page">
    <TeacherNav />

    <section class="card header-card">
      <div>
        <h2>管理员 · 作业列表</h2>
        <p class="muted">标题、所属学科、提交学生、提交时间、状态、分数、操作。</p>
      </div>
      <el-button type="primary">新建作业</el-button>
    </section>

    <section class="card">
      <TeacherAssignmentTable :assignments="assignments" />
      <Pagination :total="total" v-model:current-page="currentPage" v-model:page-size="pageSize" />
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
