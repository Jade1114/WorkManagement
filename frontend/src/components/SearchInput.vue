<template>
  <el-input
    v-bind="$attrs"
    :model-value="modelValue"
    :placeholder="placeholder"
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
