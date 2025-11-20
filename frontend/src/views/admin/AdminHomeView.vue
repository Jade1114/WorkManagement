<template>
  <div class="page">
    <section class="card header-card">
      <div>
        <h2>管理员主页</h2>
        <p class="muted">总览关键指标和快捷入口（演示数据）。</p>
      </div>
      <el-button type="primary">创建新作业</el-button>
    </section>

    <section class="stats">
      <div class="card stat" v-for="item in stats" :key="item.label">
        <p class="label">{{ item.label }}</p>
        <p class="value">{{ item.value }}</p>
      </div>
    </section>

    <section class="card quick-actions">
      <h3>快捷入口</h3>
      <div class="actions">
        <el-button type="primary" @click="$router.push('/admin/assignments')">作业列表</el-button>
        <el-button @click="$router.push('/admin/students')">学生列表</el-button>
        <el-button @click="$router.push('/admin/subjects')">学科列表</el-button>
        <el-button @click="$router.push('/login')">返回登录</el-button>
      </div>
    </section>
  </div>
</template>

<script setup>
const stats = [
  { label: '待评分', value: 12 },
  { label: '已发布作业', value: 28 },
  { label: '学生总数', value: 120 },
  { label: '学科数量', value: 6 },
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

.stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: var(--spacing-m);
}

.stat {
  padding: var(--spacing-m);
}

.label {
  color: var(--color-text-tertiary);
  margin: 0 0 6px;
}

.value {
  font-size: 22px;
  font-weight: 700;
  margin: 0;
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
