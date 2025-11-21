<template>
  <div class="page">
    <StudentNav />

    <section class="card header-card">
      <div>
        <h2>学生主页</h2>
        <p class="muted">查看近期作业与快捷入口（静态数据）。</p>
      </div>
    </section>

    <section class="card quick-actions">
      <h3>快捷入口</h3>
      <div class="actions">
        <el-button type="primary" @click="$router.push('/student/assignments')">作业列表</el-button>
        <el-button @click="$router.push('/login')">返回登录</el-button>
      </div>
    </section>

    <section class="card">
      <h3 style="padding: var(--spacing-m)">近期作业</h3>
      <el-table :data="assignments" stripe border style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="subject" label="学科" min-width="140" />
        <el-table-column prop="deadline" label="到期时间" min-width="140" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.submitted ? 'success' : 'warning'">
              {{ row.submitted ? '已提交' : '未提交' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </section>
  </div>
</template>

<script setup>
import StudentNav from '@/components/StudentNav.vue'

const assignments = [
  { id: 1, title: 'Java 基础', subject: 'Java', deadline: '2024-12-10', submitted: true },
  { id: 2, title: '数据库设计', subject: '数据库', deadline: '2024-12-12', submitted: false },
]
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
