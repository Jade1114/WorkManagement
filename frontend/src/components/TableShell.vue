<template>
  <div class="table-shell card">
    <div class="table-shell__header" v-if="$slots.title || $slots.actions">
      <div class="table-shell__title">
        <slot name="title" />
      </div>
      <div class="table-shell__actions">
        <slot name="actions" />
      </div>
    </div>

    <div class="table-shell__body">
      <el-table
        v-bind="$attrs"
        :data="data"
        :row-key="rowKey"
        :size="size"
        :height="height"
        stripe
        border
        style="width: 100%"
        v-loading="loading"
      >
        <slot />
      </el-table>
    </div>

    <div v-if="$slots.footer" class="table-shell__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup>
defineOptions({ inheritAttrs: false })

defineProps({
  data: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  rowKey: { type: [String, Function], default: undefined },
  size: { type: String, default: 'default' },
  height: { type: [Number, String], default: undefined },
})
</script>

<style scoped>
.table-shell {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-s);
  padding: var(--spacing-m);
}

.table-shell__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--spacing-m);
}

.table-shell__title {
  font-weight: 600;
}

.table-shell__actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-s);
}

.table-shell__body {
  border: 1px solid var(--color-border);
  border-radius: var(--radius);
  overflow: hidden;
}

.table-shell__footer {
  padding-top: var(--spacing-s);
}
</style>
