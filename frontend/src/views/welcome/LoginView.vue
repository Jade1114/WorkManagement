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
  if (!formRef.value) return
  try {
    const valid = await formRef.value.validate()
    if (!valid) return

    const data = await http.post('/user/auth/login', {
      username: form.username,
      password: form.password,
    })

    userStore.setLoginInfo(data)

    ElMessage.success('登录成功！')

    const role = data.role
    if (role === 'admin' || role === 'teacher') {
      router.push('/dashboard')
    } else if (role === 'student') {
      router.push('/student/home')
    } else {
      router.push('/error')
    }
  } catch (err) {
    console.error('登录出错：', err)
  }
}
</script>

<template>
  <div class="auth-page">
    <section class="auth-visual" aria-label="Work Management">
      <p class="auth-eyebrow">Work Management</p>
      <h1>今天的进度，从这里开始。</h1>
      <p>把任务收进一个安静、清楚的节奏里。</p>
    </section>
    <div class="auth-card card">
      <div class="auth-heading">
        <p class="auth-eyebrow">欢迎回来</p>
        <h2>登录</h2>
      </div>
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
          <el-button type="primary" class="auth-submit" @click="handleLogin">登录</el-button>
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
  min-height: 100%;
  display: grid;
  grid-template-columns: minmax(280px, 1fr) minmax(340px, 400px);
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xl);
}

.auth-visual {
  min-height: min(620px, calc(100vh - 120px));
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: var(--spacing-xl);
  border-radius: var(--radius-lg);
  background:
    linear-gradient(180deg, rgba(12, 30, 24, 0.12), rgba(12, 30, 24, 0.74)),
    url("https://images.unsplash.com/photo-1524995997946-a1c2e315a42f?auto=format&fit=crop&w=1100&q=80");
  background-position: center;
  background-size: cover;
  box-shadow: var(--shadow-strong);
  color: #fff;
  overflow: hidden;
}

.auth-visual h1 {
  max-width: 520px;
  margin-bottom: var(--spacing-m);
  color: #fff;
}

.auth-visual p {
  max-width: 420px;
  color: rgba(255, 255, 255, 0.86);
}

.auth-card {
  width: 100%;
  padding: var(--spacing-xl);
}

.auth-heading {
  margin-bottom: var(--spacing-l);
  text-align: left;
}

.auth-heading h2 {
  margin-bottom: 0;
}

.auth-eyebrow {
  margin: 0 0 6px;
  color: var(--color-primary-strong);
  font-size: 12px;
  font-weight: 760;
}

.auth-form {
  margin-top: var(--spacing-m);
}

.auth-submit {
  width: 100%;
}

.links {
  margin-top: var(--spacing-m);
  text-align: center;
}

@media (max-width: 820px) {
  .auth-page {
    grid-template-columns: 1fr;
  }

  .auth-visual {
    min-height: 280px;
  }
}
</style>
