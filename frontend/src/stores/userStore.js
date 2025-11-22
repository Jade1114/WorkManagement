import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    user: null
  }),
  getters: {
    username(state) {
      return state.user?.username || ''
    },
    role(state) {
      return state.user?.role || ''
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
