import { createPinia } from 'pinia'

const pinia = createPinia()

// 简易持久化：如果 store 配置了 persist: true，就用 localStorage 保存/恢复
pinia.use(({ options, store }) => {
  if (!options.persist) return

  const storageKey = `pinia-${store.$id}`
  const fromStorage = localStorage.getItem(storageKey)
  if (fromStorage) {
    try {
      store.$patch(JSON.parse(fromStorage))
    } catch (e) {
      console.warn('Failed to parse persisted state', e)
    }
  }

  store.$subscribe(
    (_mutation, state) => {
      localStorage.setItem(storageKey, JSON.stringify(state))
    },
    { detached: true }
  )
})

export default pinia
