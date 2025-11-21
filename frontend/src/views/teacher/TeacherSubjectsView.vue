<script setup>
import TeacherNav from '@/components/TeacherNav.vue'

const subjects = [
  { id: 501, name: '数据结构', teacher: 'Alex Teacher', courseCount: 8 },
  { id: 502, name: '操作系统', teacher: 'Alex Teacher', courseCount: 6 },
  { id: 503, name: '前端开发', teacher: 'Alex Teacher', courseCount: 5 },
]
</script>

<template>
  <div class="page">
    <TeacherNav />

    <section class="card header-card">
      <div>
        <h2>管理员 · 学科列表</h2>
        <p class="muted">管理学科与课程数量（静态数据示例）。</p>
      </div>
      <el-button type="primary">新建学科</el-button>
    </section>

    <section class="card table-card">
      <el-table :data="subjects" stripe border style="width: 100%">
        <el-table-column prop="id" label="学科ID" width="100" />
        <el-table-column prop="name" label="学科名称" min-width="160" />
        <el-table-column prop="teacher" label="负责人" width="140" />
        <el-table-column prop="courseCount" label="课程数量" width="120" />
        <el-table-column label="操作" width="180">
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
