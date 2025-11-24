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
    const data = await http.post('/auth/register', {
      username: form.username,
      password: form.password,
    })

    ElMessage.success('注册成功!')

    router.push('/index')
  } catch (err) {
    console.error('注册出错:', err)
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card card">
      <h2 style="text-align: center;">注册</h2>
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
          <el-button type="primary" style="width: 100%" @click="handleRegister">注册</el-button>
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

.links {
  margin-top: var(--spacing-s);
  text-align: center;
}
</style>
