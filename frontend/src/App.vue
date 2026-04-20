<script setup>
import { computed, onBeforeUnmount, watch } from 'vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { useThemeStore } from '@/stores/themeStore'

const locale = {
  ...zhCn,
  pagination: {
    ...zhCn.pagination,
    goto: '跳转至'
  }
}

const themeStore = useThemeStore()
const media = window.matchMedia('(prefers-color-scheme: dark)')
const getSystemDark = () => media.matches

const effectiveMode = computed(() => {
  if (themeStore.mode === 'auto') {
    return getSystemDark() ? 'dark' : 'light'
  }
  return themeStore.mode
})

const applyTheme = (mode) => {
  const root = document.documentElement
  root.classList.toggle('dark', mode === 'dark')
  root.classList.toggle('theme-dark', mode === 'dark')
}

const handleSystemChange = () => {
  if (themeStore.mode === 'auto') {
    applyTheme(effectiveMode.value)
  }
}

if (media.addEventListener) {
  media.addEventListener('change', handleSystemChange)
} else if (media.addListener) {
  media.addListener(handleSystemChange)
}

watch(
  () => effectiveMode.value,
  (mode) => applyTheme(mode),
  { immediate: true }
)

onBeforeUnmount(() => {
  if (media.removeEventListener) {
    media.removeEventListener('change', handleSystemChange)
  } else if (media.removeListener) {
    media.removeListener(handleSystemChange)
  }
})
</script>

<template>
  <el-config-provider :locale="locale">
    <div class="app-shell">
      <main class="app-main">
        <router-view />
      </main>
    </div>
  </el-config-provider>
</template>

<style scoped>
.app-shell {
  height: 100vh;
  background: var(--color-bg-page);
  padding: clamp(12px, 2vw, 24px);
  overflow: hidden;
}

.app-main {
  height: 100%;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
}

@media (max-width: 720px) {
  .app-shell {
    height: auto;
    min-height: 100vh;
    overflow: visible;
  }
}
</style>
