<template>
  <div class="theme-fab">
    <el-dropdown trigger="click" placement="top-start">
      <button class="fab-button" aria-label="切换主题">
        <el-icon :size="22">
          <component :is="currentIcon" />
        </el-icon>
      </button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item :icon="Sunny" @click="setMode('light')">日间模式</el-dropdown-item>
          <el-dropdown-item :icon="Moon" @click="setMode('dark')">夜间模式</el-dropdown-item>
          <el-dropdown-item :icon="SwitchButton" @click="setMode('auto')">跟随系统</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Sunny, Moon, SwitchButton } from '@element-plus/icons-vue'
import { useThemeStore } from '@/stores/themeStore'

const themeStore = useThemeStore()

const currentIcon = computed(() => {
  if (themeStore.mode === 'dark') return Moon
  if (themeStore.mode === 'light') return Sunny
  return SwitchButton
})

const setMode = (mode) => themeStore.setMode(mode)
</script>

<style scoped>
.theme-fab {
  position: fixed;
  right: 22px;
  bottom: 22px;
  z-index: 1200;
}

.fab-button {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: 1px solid rgba(99, 102, 241, 0.35);
  background: linear-gradient(135deg, rgba(99, 102, 241, 0.18), rgba(255, 255, 255, 0.06));
  color: var(--color-text-primary);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.18), 0 0 0 1px rgba(255, 255, 255, 0.04);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  display: grid;
  place-items: center;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.fab-button:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-glow);
  border-color: rgba(99, 102, 241, 0.6);
}

.fab-button:active {
  transform: translateY(0);
}
</style>
