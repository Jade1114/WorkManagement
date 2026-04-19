<template>
  <el-input
    v-bind="$attrs"
    class="search-input"
    :model-value="modelValue"
    :placeholder="placeholder"
    size="large"
    clearable
    @update:model-value="updateValue"
  >
    <template #prefix>
      <el-icon><Search /></el-icon>
    </template>
  </el-input>
</template>

<script setup>
import { Search } from "@element-plus/icons-vue";

defineProps({
  modelValue: { type: String, default: "" },
  placeholder: { type: String, default: "" },
});

const emit = defineEmits(["update:modelValue"]);

const updateValue = (val) => emit("update:modelValue", val);
</script>

<style scoped>
.search-input {
  min-width: 200px;
}

.search-input :deep(.el-input__wrapper) {
  min-height: 40px;
}
</style>
