import { defineStore } from 'pinia'
import * as authApi from '@/api/auth'

// 创建一个叫 'auth' 的 Store
export const useAuthStore = defineStore('auth', {
  // ============ 1️⃣ STATE - 数据存储 ============
  state: () => ({
    // token：登录时后端返回，用来证明身份
    token: localStorage.getItem('token') || null,
    
    // user：用户信息 { id, username, role }
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    
    // isAuthenticated：是否已登录
    isAuthenticated: !!localStorage.getItem('token'),
  }),

  // ============ 2️⃣ GETTERS - 计算属性 ============
  getters: {
    // 判断是否是教师
    isTeacher: (state) => state.user?.role === 'teacher',
    
    // 判断是否是学生
    isStudent: (state) => state.user?.role === 'student',
  },

  // ============ 3️⃣ ACTIONS - 方法 ============
  actions: {
    // 登录方法
    async login(username, password) {
      try {
        // 1. 调用 API 登录
        // 后端返回：{ token: '...', user: { id, username, role } }
        const response = await authApi.login(username, password)
        
        // 2. 保存 token 和 user 到 state
        this.token = response.token
        this.user = response.user
        this.isAuthenticated = true
        
        // 3. 保存到本地存储
        // 这样即使刷新页面也不会丢失登录信息
        localStorage.setItem('token', response.token)
        localStorage.setItem('user', JSON.stringify(response.user))
        
        // 4. 返回响应给调用者
        return response
      } catch (error) {
        // 登录失败时清除状态
        this.logout()
        throw error
      }
    },

    // 注册方法
    async register(username, password) {
      // 直接调用 API，注册后不自动登录，需要用户手动登录
      return await authApi.register(username, password)
    },

    // 退出登录方法
    async logout() {
      try {
        // 尝试调用后端退出接口
        await authApi.logout()
      } catch (error) {
        // 即使后端调用失败，我们也要清除本地状态
        console.error('Logout error:', error)
      } finally {
        // 无论成功失败都要清除本地的所有数据
        this.token = null
        this.user = null
        this.isAuthenticated = false
        localStorage.removeItem('token')
        localStorage.removeItem('user')
      }
    },

    // 页面刷新时恢复状态
    // 这个方法在 App.vue 中会被调用
    restoreAuth() {
      const token = localStorage.getItem('token')
      const user = localStorage.getItem('user')
      
      // 如果本地存储中有 token 和 user，就恢复状态
      if (token && user) {
        this.token = token
        this.user = JSON.parse(user)
        this.isAuthenticated = true
      }
    },
  },
})