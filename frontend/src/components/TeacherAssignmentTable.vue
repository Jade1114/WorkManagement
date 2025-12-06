<template>
  <TableShell :data="assignments" :loading="loading">
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
    <template #footer>
      <slot name="footer" />
    </template>
  </TableShell>
</template>

<script setup>
import TableShell from './TableShell.vue'

defineProps({
  assignments: { type: Array, default: () => [] },
  showStudent: { type: Boolean, default: true },
  loading: { type: Boolean, default: false }
})

defineEmits(['grade'])
</script>

<style scoped>
.score {
  font-weight: 600;
}

.muted {
  color: var(--color-text-tertiary);
}
</style>
