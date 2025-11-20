import http from './http'

// 注册
export const register = (username, password) => {
  return http.post('/api/auth/register', { username, password })
}

// 登录
export const login = (username, password) => {
  return http.post('/api/auth/login', { username, password })
}

// 退出登录
export const logout = () => {
  return http.post('/api/auth/logout')
}