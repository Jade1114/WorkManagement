import { defineStore } from 'pinia'

export const useThemeStore = defineStore('theme', {
  state: () => ({
    mode: 'dark' // 'light' | 'dark' | 'auto'
  }),
  actions: {
    setMode(mode) {
      this.mode = mode
    },
    toggle() {
      this.mode = this.mode === 'dark' ? 'light' : 'dark'
    },
    nextMode() {
      const order = ['light', 'dark', 'auto']
      const idx = order.indexOf(this.mode)
      this.mode = order[(idx + 1) % order.length]
    }
  },
  persist: true
})
