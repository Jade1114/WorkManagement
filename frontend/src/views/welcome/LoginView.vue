<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import http from '@/net/index.js'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const formRef = ref()
const userStore = useUserStore()

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  try {
    const data = await http.post('/auth/login', {
      username: form.username,
      password: form.password,
    })

    userStore.setLoginInfo(data)

    ElMessage.success('登录成功！')

    const role = data.role
    if (role === 'teacher') {
      router.push('/teacher/home')
    } else {
      router.push('/student/home')
    }

  } catch (err) {
    console.error('登录出错：', err)
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card card">
      <h2 style="text-align: center;">登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" class="auth-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名">
            <template #prefix><el-icon>
                <User />
              </el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码">
            <template #prefix><el-icon>
                <Lock />
              </el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="handleLogin">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="links">
        <router-link to="/register">还没有账号？去注册</router-link>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  min-height: 60vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.auth-card {
  width: 360px;
  padding: var(--spacing-l);
}

.auth-form {
  margin-top: var(--spacing-m);
}

.muted {
  color: var(--color-text-tertiary);
  margin: 0 0 var(--spacing-m);
}

.quick-nav {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-s);
  margin: var(--spacing-m) 0;
}

.links {
  margin-top: var(--spacing-s);
  text-align: center;
}
</style>
