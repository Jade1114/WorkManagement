import { defineStore } from 'pinia'

const decodeToken = (token) => {
  if (!token) return null
  const parts = token.split('.')
  if (parts.length < 2) return null
  try {
    const payload = parts[1].replace(/-/g, '+').replace(/_/g, '/')
    return JSON.parse(atob(payload))
  } catch (e) {
    return null
  }
}

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    user: null
  }),
  getters: {
    username(state) {
      return state.user?.username || decodeToken(state.token)?.username || ''
    },
    role(state) {
      return state.user?.role || decodeToken(state.token)?.role || ''
    }
  },
  actions: {
    setLoginInfo(data) {
      this.token = data.token
      this.user = {
        userId: data.userId,
        username: data.username,
        role: data.role
      }
    },
    logout() {
      this.token = ''
      this.user = null
    }
  },
  persist: true
})
