<script setup>
import StudentAssignmentTable from '@/components/StudentAssignmentTable.vue'

const assignments = [
  {
    id: 1,
    title: 'Java 基础',
    subject: 'Java',
    content: '完成基础语法练习与输出',
    deadline: '2024-12-10',
    submitted: true,
    graded: true,
  },
  {
    id: 2,
    title: '数据库设计',
    subject: '数据库',
    content: '设计ER图并撰写说明',
    deadline: '2024-12-12',
    submitted: true,
    graded: false,
  },
  {
    id: 3,
    title: '网络分层',
    subject: '计算机网络',
    content: '总结五层模型与协议作用',
    deadline: '2024-12-15',
    submitted: false,
    graded: false,
  },
]
</script>

<template>
  <div class="page">
    <section class="card header-card">
      <div>
        <h2>学生视角 · 作业列表</h2>
        <p class="muted">表头包含标题、所属学科、作业内容、到期时间、状态、操作；可过滤提交/评分状态。</p>
      </div>
    </section>

    <section class="card">
      <StudentAssignmentTable :assignments="assignments" />
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
