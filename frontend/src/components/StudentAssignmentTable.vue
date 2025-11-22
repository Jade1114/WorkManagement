<template>
  <div class="assignment-form card">
    <div class="table-container">
      <el-table :data="assignments" stripe border style="width: 100%" row-key="submissionId">
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="subject" label="所属学科" min-width="140" />
        <el-table-column prop="submitContent" label="提交内容" min-width="220" />
        <el-table-column prop="comment" label="批改内容" min-width="200">
          <template #default="{ row }">
            {{ row.comment || '未批改' }}
          </template>
        </el-table-column>
        <el-table-column prop="graded" label="状态" width="120">
          <template #default="{ row }">
            <el-tag size="small" :type="row.graded ? 'success' : 'info'">
              {{ row.graded ? '已评分' : '未评分' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="分数" width="100">
          <template #default="{ row }">
            {{ row.graded ? row.score ?? '未评分' : '未评分' }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
defineProps({
  assignments: { type: Array, default: () => [] },
})
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
</style>
