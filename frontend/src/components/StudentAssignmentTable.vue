<template>
  <div class="assignment-form card">
    <div class="filters">
      <el-select v-model="localSubject" placeholder="全部学科" style="width: 160px">
        <el-option label="全部学科" value="all" />
        <el-option v-for="s in subjects" :key="s" :label="s" :value="s" />
      </el-select>
      <el-select v-model="submitted" placeholder="提交状态" style="width: 140px">
        <el-option label="全部" value="all" />
        <el-option label="已提交" value="yes" />
        <el-option label="未提交" value="no" />
      </el-select>
      <el-select v-model="graded" placeholder="评分状态" style="width: 140px">
        <el-option label="全部" value="all" />
        <el-option label="已评分" value="yes" />
        <el-option label="未评分" value="no" />
      </el-select>
    </div>

    <div class="table-container">
      <el-table :data="filteredAssignments" stripe border style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="subject" label="所属学科" min-width="140" />
        <el-table-column prop="content" label="作业内容" min-width="220" />
        <el-table-column prop="deadline" label="到期时间" min-width="140" />
        <el-table-column prop="status" label="状态" width="140">
          <template #default="{ row }">
            <el-tag size="small" :type="row.submitted ? 'success' : 'warning'">
              {{ row.submitted ? '已提交' : '未提交' }}
            </el-tag>
            <el-tag size="small" :type="row.graded ? 'primary' : 'info'" style="margin-left: 4px">
              {{ row.graded ? '已评分' : '未评分' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default>
            <el-button size="small" type="primary">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  assignments: { type: Array, default: () => [] },
})

const subjects = computed(() => [...new Set(props.assignments.map((a) => a.subject))])
const localSubject = ref('all')
const submitted = ref('all')
const graded = ref('all')

const filteredAssignments = computed(() =>
  props.assignments.filter((a) => {
    const matchSubject = localSubject.value === 'all' || a.subject === localSubject.value
    const matchSubmitted = submitted.value === 'all' || (submitted.value === 'yes' ? a.submitted : !a.submitted)
    const matchGraded = graded.value === 'all' || (graded.value === 'yes' ? a.graded : !a.graded)
    return matchSubject && matchSubmitted && matchGraded
  })
)
</script>

<style scoped>
.assignment-form {
  padding: var(--spacing-m);
}

.filters {
  display: flex;
  gap: var(--spacing-s);
  margin-bottom: var(--spacing-m);
}

.table-container {
  background-color: var(--color-bg-card);
  border-radius: var(--radius);
  padding: var(--spacing-s);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow);
}
</style>
