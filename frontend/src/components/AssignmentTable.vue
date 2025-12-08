<template>
  <TableShell :data="assignments" :loading="loading" :row-key="rowKey">
    <el-table-column prop="title" label="标题" min-width="180" />
    <el-table-column prop="subject" label="所属学科" min-width="140" />

    <slot name="extra-columns" />

    <el-table-column
      v-if="showStudent"
      prop="student"
      label="提交学生"
      min-width="120"
    />

    <el-table-column v-if="showStatus" prop="status" label="状态" width="120">
      <template #default="{ row }">
        <slot name="status" :row="row">
          <el-tag :type="row.status === '已评分' ? 'success' : 'warning'">
            {{ row.status }}
          </el-tag>
        </slot>
      </template>
    </el-table-column>

    <el-table-column v-if="showScore" prop="score" label="分数" width="100">
      <template #default="{ row }">
        <slot name="score" :row="row">
          <span
            v-if="row.score !== null && row.score !== undefined"
            class="score"
            >{{ row.score }}</span
          >
          <span v-else class="muted">未评分</span>
        </slot>
      </template>
    </el-table-column>

    <el-table-column
      v-if="$slots.actions"
      label="操作"
      width="200"
      fixed="right"
    >
      <template #default="{ row }">
        <slot name="actions" :row="row" />
      </template>
    </el-table-column>

    <template #footer>
      <slot name="footer" />
    </template>
  </TableShell>
</template>

<script setup>
import TableShell from "./TableShell.vue";

defineProps({
  assignments: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  showStudent: { type: Boolean, default: false },
  showStatus: { type: Boolean, default: true },
  showScore: { type: Boolean, default: true },
  rowKey: { type: [String, Function], default: undefined },
});
</script>
