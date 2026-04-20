<script setup>
import { reactive, ref } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import http from '@/net/index.js'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const formRef = ref()
const form = reactive({
  username: '',
  password: '',
  confirm: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirm: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_, val, cb) => {
        if (val !== form.password) cb(new Error('两次密码不一致'))
        else cb()
      },
      trigger: 'blur',
    },
  ],
}

async function handleRegister() {
  try {
    formRef.value?.validate()
    const data = await http.post('/user/auth/register', {
      username: form.username,
      password: form.password,
    })

    ElMessage.success('注册成功!')
    router.push('/login')
  } catch (err) {
    console.error('注册出错:', err)
  }
}
</script>

<template>
  <div class="auth-page">
    <section class="auth-visual" aria-label="Work Management">
      <p class="auth-eyebrow">Work Management</p>
      <h1>给新的协作，留一个清楚的位置。</h1>
      <p>从账号开始，把之后的学习和提交接住。</p>
    </section>
    <div class="auth-card card">
      <div class="auth-heading">
        <p class="auth-eyebrow">新账号</p>
        <h2>注册</h2>
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
        <el-form-item label="确认密码" prop="confirm">
          <el-input v-model="form.confirm" type="password" placeholder="请再次输入密码">
            <template #prefix><el-icon>
                <Lock />
              </el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="auth-submit" @click="handleRegister">注册</el-button>
        </el-form-item>
      </el-form>
      <div class="links">
        <router-link to="/login">已有账号？去登录</router-link>
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
    linear-gradient(180deg, rgba(12, 30, 24, 0.1), rgba(12, 30, 24, 0.74)),
    url("https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=1100&q=80");
  background-position: center;
  background-size: cover;
  box-shadow: var(--shadow-strong);
  color: #fff;
  overflow: hidden;
}

.auth-visual h1 {
  max-width: 560px;
  margin-bottom: var(--spacing-m);
  color: #fff;
}

.auth-visual p {
  max-width: 440px;
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
