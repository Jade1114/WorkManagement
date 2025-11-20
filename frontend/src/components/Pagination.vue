<template>
  <div class="pagination-container card">
    <span>共 {{ total }} 条</span>
    <el-pagination
      v-model:current-page="localCurrentPage"
      v-model:page-size="localPageSize"
      :page-sizes="pageSizes"
      layout="sizes, prev, pager, next, jumper"
      :total="total"
    />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  total: { type: Number, required: true },
  currentPage: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 },
  pageSizes: { type: Array, default: () => [5, 10, 20, 50] },
})

const emit = defineEmits(['update:currentPage', 'update:pageSize'])

const localCurrentPage = ref(props.currentPage)
const localPageSize = ref(props.pageSize)

watch(localCurrentPage, (val) => emit('update:currentPage', val))
watch(localPageSize, (val) => emit('update:pageSize', val))
</script>

<style scoped>
.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-m);
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
}
</style>
