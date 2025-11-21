<script setup>
import TeacherNav from '@/components/TeacherNav.vue'

const students = [
  { id: 301, name: 'Alice', major: '计算机科学', email: 'alice@example.com', progress: '优' },
  { id: 302, name: 'Bob', major: '软件工程', email: 'bob@example.com', progress: '良' },
  { id: 303, name: 'Charlie', major: '网络安全', email: 'charlie@example.com', progress: '中' },
]
</script>

<template>
  <div class="page">
    <TeacherNav />

    <section class="card header-card">
      <div>
        <h2>管理员 · 学生列表</h2>
        <p class="muted">查看学生信息（静态数据示例）。</p>
      </div>
      <el-button type="primary">新增学生</el-button>
    </section>

    <section class="card table-card">
      <el-table :data="students" stripe border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" min-width="120" />
        <el-table-column prop="major" label="专业" min-width="140" />
        <el-table-column prop="email" label="邮箱" min-width="200" />
        <el-table-column prop="progress" label="表现" width="120">
          <template #default="{ row }">
            <el-tag :type="row.progress === '优' ? 'success' : row.progress === '良' ? 'info' : 'warning'">
              {{ row.progress }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default>
            <el-button size="small" type="primary" text>编辑</el-button>
            <el-button size="small" type="danger" text>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
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

.table-card {
  padding: var(--spacing-m);
}
</style>
