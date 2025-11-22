<template>
  <div class="assignment-form card">
    <div class="table-container">
      <el-table :data="assignments" stripe border style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="subject" label="所属学科" min-width="140" />
        <el-table-column v-if="showStudent" prop="student" label="提交学生" min-width="120" />
        <el-table-column v-if="showStudent" prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === '已评分' ? 'success' : 'warning'">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="showStudent" prop="score" label="分数" width="100">
          <template #default="{ row }">
            <span v-if="row.score !== null" class="score">{{ row.score }}</span>
            <span v-else class="muted">未评分</span>
          </template>
        </el-table-column>
        <el-table-column v-if="showStudent" label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="$emit('grade', row)">评分</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
defineProps({
  assignments: { type: Array, default: () => [] },
  showStudent: { type: Boolean, default: true }
})

defineEmits(['grade'])
</script>

<style scoped>
.assignment-form {
  padding: var(--spacing-m);
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
