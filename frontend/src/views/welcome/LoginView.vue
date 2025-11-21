<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref()
const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = () => {
  formRef.value?.validate()
}

const goTeacher = () => router.push('/teacher/home')
const goStudent = () => router.push('/student/home')
</script>

<template>
  <div class="auth-page">
    <div class="auth-card card">
      <h2>登录</h2>
      <p class="muted">此处仅展示 UI，未接入接口。</p>
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

      <div class="quick-nav">
        <el-button type="success" plain @click="goTeacher">直接进入教师主页</el-button>
        <el-button type="info" plain @click="goStudent">直接进入学生主页</el-button>
      </div>

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
